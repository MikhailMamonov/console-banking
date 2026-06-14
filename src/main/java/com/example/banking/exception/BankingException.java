package com.example.banking.exception;

public class BankingException extends RuntimeException {
    private final String operation;
    private final ErrorType errorType;
    private final String userMessage;

    public BankingException(String operation, String message) {
        super(message);
        this.operation = operation;
        this.errorType = ErrorType.VALIDATION_ERROR;
        this.userMessage = message;
    }

    public BankingException(String operation, ErrorType errorType, String message) {
        super(message);
        this.operation = operation;
        this.errorType = errorType;
        this.userMessage = message;
    }

    public BankingException(String operation, ErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.errorType = errorType;
        this.userMessage = message;
    }

    public String getOperation() {
        return operation;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public String getUserMessage() {
        return userMessage;
    }

    @Override
    public String toString() {
        return String.format("BankingException[operation=%s, type=%s, message=%s]",
                operation, errorType.getDescription(), getUserMessage());
    }
}