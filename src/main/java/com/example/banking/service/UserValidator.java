package com.example.banking.service;

import com.example.banking.exception.BankingException;
import com.example.banking.exception.ErrorType;
import com.example.banking.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Валидатор для операций с пользователями.
 */
@Component
public class UserValidator {

    public void validateLoginFormat(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Login cannot be null or empty");
        }
    }

    public void validateLoginUniqueness(String login, boolean exists) {
        if (exists) {
            throw new IllegalArgumentException("User with login '" + login + "' already exists");
        }
    }

    public void validateNotEmpty(String value, String fieldName, String operationType) {
        if (value == null || value.trim().isEmpty()) {
            throw new BankingException(operationType, ErrorType.VALIDATION_ERROR,
                    String.format("%s cannot be null or empty", fieldName));
        }
    }

    public void validateLoginAvailabilityForUpdate(String newLogin, String currentLogin, boolean isTaken) {
        if (!newLogin.equals(currentLogin) && isTaken) {
            throw new BankingException("USER_UPDATE", ErrorType.VALIDATION_ERROR,
                    String.format("Login '%s' is already taken", newLogin));
        }
    }

    public User validateUserPresent(Optional<User> userOpt, String userId, String operationType) {
        return userOpt.orElseThrow(() -> new BankingException(operationType, ErrorType.USER_NOT_FOUND,
                String.format("User with ID '%s' not found", userId)));
    }
}