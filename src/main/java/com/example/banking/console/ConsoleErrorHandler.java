package com.example.banking.console;

import com.example.banking.exception.BankingException;
import com.example.banking.exception.ErrorType;
import org.springframework.stereotype.Component;

@Component
public class ConsoleErrorHandler {

    private final ConsoleMenu consoleMenu;

    public ConsoleErrorHandler(ConsoleMenu consoleMenu) {
        this.consoleMenu = consoleMenu;
    }

    public void handleBankingException(BankingException e) {
        System.out.println(); // Пустая строка перед ошибкой
        System.err.flush();

        System.err.printf("❌ Error in %s: %s%n", e.getOperationType(), e.getMessage());

        String suggestion = getSuggestionForError(e.getErrorType());
        if (suggestion != null) {
            System.err.printf("   💡 %s%n", suggestion);
        }

        if (e.getDetails() != null && e.getDetails().length > 0) {
            System.err.print("   Details: ");
            for (int i = 0; i < e.getDetails().length; i += 2) {
                if (i + 1 < e.getDetails().length) {
                    System.err.printf("%s=%s ", e.getDetails()[i], e.getDetails()[i + 1]);
                }
            }
            System.err.println();
        }

        System.err.flush();
    }

    private String getSuggestionForError(ErrorType errorType) {
        switch (errorType) {
            case INSUFFICIENT_FUNDS:
                return "Please check your balance and try again with a smaller amount";
            case ACCOUNT_NOT_FOUND:
                return "Account does not exist. Please verify the account ID";
            case USER_NOT_FOUND:
                return "User does not exist. Please verify the user ID";
            case VALIDATION_ERROR:
                return "Please check your input and try again";
            default:
                return null;
        }
    }
}