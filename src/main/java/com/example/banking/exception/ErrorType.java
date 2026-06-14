package com.example.banking.exception;

public enum ErrorType {
    VALIDATION_ERROR("Validation Error"),
    ACCOUNT_NOT_FOUND("Account Not Found"),
    USER_NOT_FOUND("User Not Found"),
    INSUFFICIENT_FUNDS("Insufficient Funds"),
    ACCOUNT_CLOSE_ERROR("Account Close Error"),
    TRANSFER_ERROR("Transfer Error"),
    WITHDRAW_ERROR("Withdraw Error"),
    DEPOSIT_ERROR("Deposit Error"),
    CONFIGURATION_ERROR("Configuration Error");

    private final String description;

    ErrorType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}