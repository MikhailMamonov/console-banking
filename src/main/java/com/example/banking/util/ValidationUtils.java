package com.example.banking.util;

import com.example.banking.exception.BankingException;
import com.example.banking.exception.ErrorType;

import java.util.Optional;
import java.util.function.Supplier;

public class ValidationUtils {

    public static void validateNotEmpty(String value, String fieldName, String operationType) {
        if (value == null || value.trim().isEmpty()) {
            throw new BankingException(operationType, ErrorType.VALIDATION_ERROR,
                    String.format("%s cannot be empty!", fieldName));
        }
    }

    public static void validatePositiveAmount(double amount, String operationType) {
        if (amount <= 0) {
            throw new BankingException(operationType, ErrorType.VALIDATION_ERROR,
                    String.format("Amount must be positive! Current amount: %.2f", amount));
        }
    }

    public static <T> T findOrThrow(Optional<T> optional, String entityName, String id, String operationType) {
        return optional.orElseThrow(() -> new BankingException(operationType, ErrorType.NOT_FOUND,
                String.format("%s with ID '%s' not found!", entityName, id)));
    }

    public static void validateCondition(boolean condition, String message, String operationType) {
        if (!condition) {
            throw new BankingException(operationType, ErrorType.VALIDATION_ERROR, message);
        }
    }
}