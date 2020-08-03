package com.vj.kafka.elastic.repository;

import com.vj.kafka.elastic.model.Account;
import com.vj.kafka.elastic.model.Transaction;

import java.util.Optional;

public interface IAccountIndexRepository {
    public Optional<Account> getAccount(String accountId);
    public boolean createAccount(Account account);
    public boolean updateAccount(Account account);
    public boolean addAccountTransaction(Account account);
    public boolean isAccountExists(String accountId);
}
