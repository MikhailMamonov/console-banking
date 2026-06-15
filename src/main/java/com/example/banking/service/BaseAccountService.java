package com.example.banking.service;

import com.example.banking.exception.BankingException;
import com.example.banking.exception.ErrorHandler;
import com.example.banking.exception.ErrorType;
import com.example.banking.model.entity.Account;
import com.example.banking.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.example.banking.util.ValidationUtils.*;

public abstract class BaseAccountService {


    @Autowired
    protected UserService userService;

    @Autowired
    protected ErrorHandler errorHandler;

    protected <T> T executeWithErrorHandling(String operationType, Supplier<T> operation) {
        try {
            return operation.get();
        } catch (BankingException e) {
            errorHandler.handleError(e);
            return null;
        } catch (Exception e) {
            errorHandler.handleError(operationType, "Unexpected error: " + e.getMessage());
            return null;
        }
    }

    protected void executeVoidWithErrorHandling(String operationType, Runnable action) {
        try {
            action.run();
        } catch (BankingException e) {
            // Очищаем консоль или добавляем перенос строки перед ошибкой
            System.err.println(); // Пустая строка перед ошибкой
            System.err.printf("❌ Error in %s: %s%n", e.getOperationType(), e.getMessage());

            // Дополнительная информация об ошибке
            if (e.getErrorType() != null) {
                System.err.printf("   Error type: %s%n", e.getErrorType());
            }

        } catch (Exception e) {
            System.err.println();
            System.err.printf("❌ Unexpected error in %s: %s%n", operationType, e.getMessage());
            e.printStackTrace();
        }
    }

    protected Account findAccountOrThrow(String accountId, String operationType) {
        return findAccountById(accountId)
                .orElseThrow(() -> new BankingException(operationType, ErrorType.NOT_FOUND,
                        String.format("Account with ID '%s' not found!", accountId)));
    }

    protected User findUserOrThrow(String userId, String operationType) {
        return userService.findUserById(userId)
                .orElseThrow(() -> new BankingException(operationType, ErrorType.USER_NOT_FOUND,
                        String.format("User with ID '%s' not found!", userId)));
    }

    protected Optional<Account> findAccountById(String accountId) {
        return userService.getAllUsers().stream()
                .flatMap(user -> user.getAccountList().stream())
                .filter(account -> account.getId().equals(accountId))
                .findFirst();
    }

    protected void updateBalance(Account account, double amount, String operationType, String actionName) {
        double newBalance = account.getMoneyAmount() + amount;
        account.setMoneyAmount(newBalance);
        System.out.printf("Amount %.2f %s to account ID: %s. New balance: %.2f%n",
                Math.abs(amount), actionName, account.getId(), newBalance);
    }

    protected void validateCommonParameters(String accountId, double amount, String operationType) {
        validateNotEmpty(accountId, "Account ID", operationType);
        validatePositiveAmount(amount, operationType);
    }
}