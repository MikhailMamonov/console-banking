package com.example.banking.exception;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class ErrorHandler {

    public void handleError(BankingException e) {
        System.err.printf("Error in %s: %s%n", e.getOperationType(), e.getMessage());
        if (e.getDetails() != null && e.getDetails().length > 0) {
            System.err.printf("Details: %s%n",
                    Arrays.stream(e.getDetails())
                            .map(Object::toString)
                            .collect(Collectors.joining(", ")));;
        }
    }

    public void handleError(String operationType, String message) {
        System.err.printf("Error in %s: %s%n", operationType, message);
    }
}