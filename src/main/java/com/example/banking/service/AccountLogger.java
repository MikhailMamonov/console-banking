package com.example.banking.service;

import com.example.banking.model.entity.Account;
import com.example.banking.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountLogger {

    // Константы для шаблонов сообщений
    private static final String ACCOUNTS_HEADER = "\nAccounts for user: %s";
    private static final String NO_ACCOUNTS_FOUND = "  No accounts found";
    private static final String ACCOUNT_DISPLAY = "  ID: %s, Balance: %.2f";
    private static final String TRANSFER_TEMPLATE = "Amount %.2f transferred from account %s to account %s";
    private static final String TRANSFER_WITH_COMMISSION = "Amount %.2f transferred from account %s to account %s (commission: %.2f)";
    private static final String DEPOSIT_TEMPLATE = "Amount %.2f deposited to account %s";
    private static final String WITHDRAWAL_TEMPLATE = "Amount %.2f withdrawn from account %s";
    private static final String ACCOUNT_CLOSURE_TEMPLATE = "Account %s successfully closed. Funds transferred to account %s";
    private static final String FUNDS_TRANSFER_TEMPLATE = "Transferred %.2f from account %s to account %s";

    // Новые константы для UserService
    private static final String NO_USERS_MESSAGE = "Нет зарегистрированных пользователей";
    private static final String USERS_HEADER = "\nList of all users:";
    private static final String USER_CREATED_TEMPLATE = "User created successfully: %s (ID: %s)";
    private static final String USER_DELETED_TEMPLATE = "User deleted successfully: %s (ID: %s)";
    private static final String USER_NOT_FOUND_TEMPLATE = "User not found: %s";
    private static final String USER_LOGIN_UPDATED_TEMPLATE = "User login updated from '%s' to '%s'";
    private static final String ALL_USERS_CLEARED_TEMPLATE = "All users cleared. Total removed: %d";

    /**
     * Логирует обновление логина пользователя
     */
    public void logUserLoginUpdated(String oldLogin, String newLogin) {
        System.out.printf(USER_LOGIN_UPDATED_TEMPLATE + "%n", oldLogin, newLogin);
    }

    /**
     * Логирует очистку всех пользователей
     */
    public void logAllUsersCleared(int count) {
        System.out.printf(ALL_USERS_CLEARED_TEMPLATE + "%n", count);
    }
    /**
     * Логирует операцию пополнения счета
     */
    public void logDeposit(String accountId, double amount) {
        System.out.printf(DEPOSIT_TEMPLATE + "%n", amount, accountId);
    }

    /**
     * Логирует операцию снятия средств
     */
    public void logWithdrawal(String accountId, double amount) {
        System.out.printf(WITHDRAWAL_TEMPLATE + "%n", amount, accountId);
    }

    /**
     * Логирует операцию перевода между счетами
     */
    public void logTransfer(String sourceId, String targetId, double amount, double commission) {
        if (commission > 0) {
            System.out.printf(TRANSFER_WITH_COMMISSION + "%n", amount, sourceId, targetId, commission);
        } else {
            System.out.printf(TRANSFER_TEMPLATE + "%n", amount, sourceId, targetId);
        }
    }

    /**
     * Логирует отображение списка счетов пользователя
     */
    public void logUserAccounts(User user) {
        System.out.printf(ACCOUNTS_HEADER, user.getLogin());
        System.out.println();

        if (user.getAccountList().isEmpty()) {
            System.out.println(NO_ACCOUNTS_FOUND);
        } else {
            user.getAccountList().forEach(this::logAccountDetails);
        }
    }

    /**
     * Логирует детали одного счета
     */
    private void logAccountDetails(Account account) {
        System.out.printf(ACCOUNT_DISPLAY + "%n", account.getId(), account.getMoneyAmount());
    }

    /**
     * Логирует создание аккаунта
     * */
    public void logAccountCreation(Account account) {
        System.out.printf("✅ Account created: ID=%s, UserID=%s, Balance=%.2f%n",
                account.getId(), account.getUserId(), account.getMoneyAmount());
    }
    /**
     * Логирует закрытие счета
     */
    public void logAccountClosure(String closedId, String targetId) {
        System.out.printf(ACCOUNT_CLOSURE_TEMPLATE + "%n", closedId, targetId);
    }

    /**
     * Логирует перевод средств при закрытии счета
     */
    public void logFundsTransfer(double amount, String sourceId, String targetId) {
        System.out.printf(FUNDS_TRANSFER_TEMPLATE + "%n", amount, sourceId, targetId);
    }

    // ============ НОВЫЕ МЕТОДЫ ДЛЯ USER SERVICE ============

    /**
     * Логирует отображение всех пользователей
     */
    public void logAllUsers(List<User> users) {
        if (users.isEmpty()) {
            System.out.println(NO_USERS_MESSAGE);
        } else {
            System.out.println(USERS_HEADER);
            users.forEach(this::logUserDetails);
        }
    }

    /**
     * Логирует детали одного пользователя
     */
    private void logUserDetails(User user) {
        System.out.println(user);
    }

    /**
     * Логирует создание пользователя
     */
    public void logUserCreated(User user) {
        System.out.printf(USER_CREATED_TEMPLATE + "%n", user.getLogin(), user.getId());
    }

    /**
     * Логирует удаление пользователя
     */
    public void logUserDeleted(User user) {
        System.out.printf(USER_DELETED_TEMPLATE + "%n", user.getLogin(), user.getId());
    }

    /**
     * Логирует ошибку - пользователь не найден
     */
    public void logUserNotFound(String identifier) {
        System.out.printf(USER_NOT_FOUND_TEMPLATE + "%n", identifier);
    }
}