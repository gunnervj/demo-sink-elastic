package com.vj.kafka.elastic.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.vj.kafka.elastic.Constants;
import com.vj.kafka.elastic.model.Account;
import com.vj.kafka.elastic.model.Transaction;
import com.vj.kafka.elastic.repository.AccountIndexRepository;
import com.vj.kafka.elastic.repository.CommonIndexRepository;
import com.vj.kafka.elastic.repository.IAccountIndexRepository;
import com.vj.kafka.elastic.repository.ICommonIndexRepository;
import com.vj.kafka.elastic.service.model.SourceDataModel;
import com.vj.kafka.elastic.service.util.LocalDateTimeDeserializer;
import com.vj.kafka.elastic.service.util.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.*;

@Slf4j
public class ElasticAccountTaskService implements TaskService {
    private final IAccountIndexRepository accountIndexRepository;
    private final Gson gson;

    public ElasticAccountTaskService(RestHighLevelClient elasticClient) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
        gson = gsonBuilder.setPrettyPrinting().create();
        this.accountIndexRepository = new AccountIndexRepository(elasticClient, gson);
        checkAndInitializeIndexInElasticSearch(elasticClient);
    }

    private void checkAndInitializeIndexInElasticSearch(RestHighLevelClient elasticClient) {
        ICommonIndexRepository indexRepo = new CommonIndexRepository(elasticClient);
        if (!indexRepo.isIndexExist(Constants.ACCOUNT_INDEX)){
            log.info("Index [{}] does not exist in elastic search. Will attempt to create and empty index.",
                    Constants.ACCOUNT_INDEX);
            if (indexRepo.createEmptyIndex(Constants.ACCOUNT_INDEX) == true) {
                log.info("Created Empty Index [{}]", Constants.ACCOUNT_INDEX);
            } else {
                log.info("Error while initializing Index [{}]", Constants.ACCOUNT_INDEX);
            }
        }
    }

    public boolean process(String record) {
        boolean isSuccess = false;
        Optional<SourceDataModel> sourceOpt = parseSourceData(record);
        if (sourceOpt.isPresent()) {
            SourceDataModel source = sourceOpt.get();
            Account account = null;
            if (source.getRequestType() != null) {
                switch (source.getRequestType()) {
                    case ACCOUNT_CREATE:
                        account = transformSourceToAccount(source, false);
                        isSuccess = performCreateAccountAction(account);
                        break;
                    case ADD_TRANSACTION:
                        account = transformSourceToAccount(source, false);
                        isSuccess = performAddAccountTransaction(account);
                        break;
                    case ACCOUNT_UPDATE:
                        account = transformSourceToAccount(source, true);
                        isSuccess = performUpdateAccount(account);
                        break;
                    default:
                        log.error("Invalid request type : {}", source.getRequestType());
                }
            } else {
                log.error("Invalid request type : {}", source.getRequestType());
            }
        }

        return isSuccess;
    }

    private boolean performCreateAccountAction(Account account) {
        boolean isSuccess = false;
        if (!account.isValid()) {
            log.error("Invalid account in request. Account :  {}", gson.toJson(account));
        } else if (accountIndexRepository.isAccountExists(account.getAccountId())) {
            log.error("Cannot create new Account with ID {}", account.getAccountId());
        } else {
            isSuccess = accountIndexRepository.createAccount(account);
        }
        return isSuccess;
    }

    private boolean performUpdateAccount(Account account) {
        boolean isSuccess = false;
        if (!accountIndexRepository.isAccountExists(account.getAccountId())) {
            log.error("Cannot create new Account with ID {}", account.getAccountId());
            isSuccess = false;
        } else {
            isSuccess = accountIndexRepository.updateAccount(account);
        }

        return isSuccess;
    }

    private boolean performAddAccountTransaction(Account account) {
        boolean isSuccess = false;
        if (!accountIndexRepository.isAccountExists(account.getAccountId())) {
            log.error("Cannot add transaction to Account with ID {} : Account does not exist.", account.getAccountId());
            isSuccess = false;
        } else {
            if ( account.getAccountBalance() != null || account.getAccountBalance() > 0 ) {
                isSuccess = accountIndexRepository.addAccountTransaction(account);
            } else {
                log.info("Cannot add transaction to Account [{}] as account balance is missing.", account.getAccountId());
            }
        }

        return isSuccess;
    }

    private Optional<SourceDataModel> parseSourceData(String record) {
        try {
            SourceDataModel source = gson.fromJson(record, SourceDataModel.class);
            return Optional.of(source);
        } catch (JsonSyntaxException e) {
            log.error("Error while parsing source data {}", record, e);
        }
        return Optional.empty();
    }

    private Account transformSourceToAccount(SourceDataModel source, boolean excludeTransaction) {
        List<Transaction> transactions = null;
        log.info("Transforming : " + source.toString());
        ZonedDateTime zdt = ZonedDateTime.of(source.getTransactionDate(), ZoneId.systemDefault());
        if (!excludeTransaction) {
            Transaction transaction = Transaction.builder()
                    .transactionId(source.getTransactionId())
                    .type(source.getType())
                    .transactionDate(zdt.toInstant().toEpochMilli())
                    .amount(source.getAmount())
                    .build();
            if (null != transaction && transaction.isValid()) {
                transactions = new ArrayList<>();
                transactions.add(transaction);
            }
        }
        return Account.builder()
                .accountBalance(source.getAccountBalance())
                .accountId(source.getAccountId())
                .accountName(source.getAccountName())
                .accountBalance(source.getAccountBalance())
                .transactions(transactions)
                .build();
    }

}
