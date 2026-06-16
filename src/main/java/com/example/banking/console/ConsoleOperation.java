package com.example.banking.console;

/**
 * Enum representing all available console operation types.
 */
public enum ConsoleOperation {
    USER_CREATE("Create new user"),
    SHOW_ALL_USERS("Show all users"),
    SHOW_USER_ACCOUNTS("Show user accounts"),
    ACCOUNT_CREATE("Create new account"),
    ACCOUNT_CLOSE("Close account"),
    ACCOUNT_DEPOSIT("Deposit to account"),
    ACCOUNT_WITHDRAW("Withdraw from account"),
    ACCOUNT_TRANSFER("Transfer between accounts"),
    EXIT("Exit");

    private final String description;

    ConsoleOperation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static ConsoleOperation fromString(String value) {
        try {
            return ConsoleOperation.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}