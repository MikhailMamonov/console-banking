package com.example.banking.service;

import com.example.banking.exception.BankingException;
import com.example.banking.exception.ErrorType;
import com.example.banking.model.entity.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccountValidator {

    @Value("${account.default-amount:0.0}")
    private double defaultAmount;

    @Value("${account.transfer-commission:0.0}")
    private double transferCommission;

    @Value("${account.maximum-balance:1000000.0}")
    private double maximumBalance;

    public void validateNotEmpty(String value, String fieldName, String operationType) {
        if (value == null || value.trim().isEmpty()) {
            throw new BankingException(operationType, ErrorType.VALIDATION_ERROR,
                    String.format("%s cannot be empty", fieldName));
        }
    }

    public void validatePositiveAmount(double amount, String operationType) {
        if (amount <= 0) {
            throw new BankingException(operationType, ErrorType.VALIDATION_ERROR,
                    String.format("Amount must be positive: %.2f", amount));
        }
    }

    public void validateTransaction(String accountId, double amount, String operationType) {
        validateNotEmpty(accountId, "Account ID", operationType);
        validatePositiveAmount(amount, operationType);
    }

    public void validateUser(String userId, String operationType) {
        validateNotEmpty(userId, "User ID", operationType);
    }

    public void validateCondition(boolean condition, String message, String operationType) {
        if (!condition) {
            throw new BankingException(operationType, ErrorType.VALIDATION_ERROR, message);
        }
    }

    public void validateSufficientFunds(Account account, double requiredAmount, double commission) {
        if (account.getMoneyAmount() < requiredAmount) {
            if (commission > 0) {
                throw new BankingException("TRANSFER", ErrorType.INSUFFICIENT_FUNDS,
                        String.format("Insufficient funds! Available: %.2f, Required: %.2f (amount: %.2f + commission: %.2f)",
                                account.getMoneyAmount(), requiredAmount, requiredAmount - commission, commission));
            }
            throw new BankingException("TRANSFER", ErrorType.INSUFFICIENT_FUNDS,
                    String.format("Insufficient funds! Available: %.2f, Required: %.2f", account.getMoneyAmount(), requiredAmount));
        }
    }

    public void validateTransfer(String sourceId, String targetId, double amount) {
        validateNotEmpty(sourceId, "Source Account ID", "TRANSFER");
        validateNotEmpty(targetId, "Target Account ID", "TRANSFER");
        validatePositiveAmount(amount, "TRANSFER");
    }
}