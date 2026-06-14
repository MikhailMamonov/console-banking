package com.example.banking.exception;

import org.springframework.stereotype.Component;

@Component
public class ErrorHandler {

    public void handleError(BankingException e) {
        System.err.println(formatError(e));
        logError(e);
    }

    public void handleError(IllegalArgumentException e) {
        System.err.println(formatError(e));
        logError(e);
    }

    public void handleError(Exception e) {
        System.err.println(formatError(e));
        logError(e);
    }

    public void handleError(String operation, String message) {
        System.err.println(String.format("[ERROR] Operation: %s | Message: %s", operation, message));
        logError(operation, message);
    }

    private String formatError(BankingException e) {
        return String.format("[ERROR] Operation: %s | Type: %s | Message: %s",
                e.getOperation(),
                e.getErrorType() != null ? e.getErrorType().getDescription() : "UNKNOWN",
                e.getMessage());
    }

    private String formatError(IllegalArgumentException e) {
        return String.format("[ERROR] Validation Error: %s", e.getMessage());
    }

    private String formatError(Exception e) {
        return String.format("[ERROR] Unexpected Error: %s", e.getMessage());
    }

    private void logError(Exception e) {
        // Здесь можно добавить логирование в файл
        // Например, используя SLF4J:
        // logger.error(e.getMessage(), e);
    }

    private void logError(String operation, String message) {
        // Логирование ошибки без исключения
        // logger.error("Operation: {} - {}", operation, message);
    }
}