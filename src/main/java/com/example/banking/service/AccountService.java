package com.example.banking.service;

import com.example.banking.model.entity.Account;

import java.util.List;

/**
 * Service interface for account operations.
 */
public interface AccountService {

    /**
     * Creates a new account for a specific user.
     */
    Account createAccountForUser(String userId);

    /**
     * Creates a new account with optional initial balance.
     */
    Account createAccount(String userId, Double moneyAmount);

    /**
     * Displays all accounts belonging to a specific user.
     */
    void showUserAccounts(String userId);
/**
 *
 * **/
    List<Account> findAccountsByUserId(String userId);

    /**
     * Deposits a specified amount into an account.
     */
    void deposit(String accountId, double amount);

    /**
     * Withdraws a specified amount from an account.
     */
    void withdraw(String accountId, double amount);

    /**
     * Transfers funds from one account to another.
     */
    void transfer(String sourceId, String targetId, double amount);

    /**
     * Closes an account and optionally transfers remaining funds to another account.
     */
    void closeAccount(String accountId, String targetAccountId);

    Account getAccountById(String accountId);
}