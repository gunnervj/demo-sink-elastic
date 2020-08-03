package com.vj.kafka.elastic.repository;


import com.google.gson.Gson;
import com.vj.kafka.elastic.Constants;
import com.vj.kafka.elastic.model.Account;
import com.vj.kafka.elastic.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class AccountIndexRepository implements IAccountIndexRepository {
    private RestHighLevelClient elasticClient;
    private Gson gson;

    public AccountIndexRepository(RestHighLevelClient elasticClient, Gson gson) {
        this.elasticClient = elasticClient;
        this.gson = gson;
    }

    public Optional<Account> getAccount(String accountId) {
        GetRequest getAccountRequest = new GetRequest(Constants.ACCOUNT_INDEX, accountId);

        try {
            FetchSourceContext fetchSourceContext =
                    new FetchSourceContext(true, Constants.INCLUDES , Constants.EXCLUDES);
            GetResponse response = elasticClient.get(getAccountRequest, RequestOptions.DEFAULT);
            if (null != response && response.isExists()) {
                Account account = gson.fromJson(response.getSourceAsString(), Account.class);
                return Optional.of(account);
            }
        } catch (Exception ex) {
            log.error("Error while fetching account {}", accountId, ex);
        }

        return Optional.empty();
    }

    public boolean isAccountExists(String accountId) {
        GetRequest getAccountRequest = new GetRequest(Constants.ACCOUNT_INDEX, accountId);
        try {
            getAccountRequest.fetchSourceContext(new FetchSourceContext(false));
            getAccountRequest.storedFields("_none_");
            return elasticClient.exists(getAccountRequest, RequestOptions.DEFAULT);
        } catch (Exception ex) {
            log.error("Error while fetching account {}", accountId, ex);
        }
        return false;
    }


    public boolean createAccount(Account account) {
        IndexRequest indexRequest = new IndexRequest(Constants.ACCOUNT_INDEX);
        indexRequest.id(account.getAccountId());
        boolean isSuccess = false;
        try {
            indexRequest.source(gson.toJson(account), XContentType.JSON);
            IndexResponse response = elasticClient.index(indexRequest, RequestOptions.DEFAULT);
            if (response.getResult() == DocWriteResponse.Result.CREATED) {
                isSuccess = true;
                log.info("Create Account Id {} with details : ", account.getAccountId(), gson.toJson(account));
            }
        } catch (Exception ex) {
            log.error("Error while creating a new account with id {}. Account Details: ",
                    account.getAccountId(),
                    account.toString(),
                    ex);
        }
        return isSuccess;
    }

    public boolean updateAccount(Account account) {
        boolean isSuccess = false;

        UpdateRequest request = new UpdateRequest(Constants.ACCOUNT_INDEX, account.getAccountId());
        try {
            request.doc(gson.toJson(account), XContentType.JSON);
            UpdateResponse response = elasticClient.update(request, RequestOptions.DEFAULT);
            if (response.getResult() == DocWriteResponse.Result.UPDATED) {
               isSuccess = true;
               log.info("Updated Account Id {} with details : ", account.getAccountId(), gson.toJson(account));
            }
        } catch (Exception ex) {
            log.error("Error while updating the account with id  {}. Account Details : " ,
                    account.getAccountId(),
                    gson.toJson(account),
                    ex);
        }

        return isSuccess;
    }

    public boolean addAccountTransaction(Account account) {
        boolean isSuccess = false;

        UpdateRequest request = new UpdateRequest(Constants.ACCOUNT_INDEX, account.getAccountId());
        Map<String, Object> params = new HashMap<>();

        if(account.getTransactions().isEmpty()) {
            return isSuccess;
        }

        Transaction transaction = account.getTransactions().get(0);
        params.put("amount", transaction.getAmount());
        params.put("transactionDate", transaction.getTransactionDate());
        params.put("transactionId", transaction.getTransactionId());
        params.put("type", transaction.getType());
        try {
            Script inline = new Script(ScriptType.INLINE, "painless",
                            "if(ctx._source.transactions != null) { " +
                            "ctx._source.transactions.add(params.transaction); " +
                            "} else { " +
                            "ctx._source.transactions= []; ctx._source.transactions.add(params.transaction); " +
                            "}",
                    Collections.singletonMap("transaction", params));
            request.script(inline);
            UpdateResponse response = elasticClient.update(request, RequestOptions.DEFAULT);
            if (response.getResult() == DocWriteResponse.Result.UPDATED) {
                isSuccess = true;
                log.info("Added Transaction to Account Id {} with details : ", account.getAccountId(), gson.toJson(transaction));
                Account updateAccount = Account.builder()
                                                .accountBalance(account.getAccountBalance())
                                                .accountId(account.getAccountId())
                                                .build();
                isSuccess = updateAccount(updateAccount);
            }

        } catch (Exception ex) {
            log.error("Error while adding transaction to account {} : Transaction Details: ",
                    account.getAccountId(),
                    gson.toJson(transaction),
                    ex);
        }
        return isSuccess;
    }

}
