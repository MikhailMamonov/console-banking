package com.example.banking.console.handler;

import com.example.banking.console.ConsoleMenu;
import com.example.banking.console.ConsoleErrorHandler;
import com.example.banking.exception.BankingException;
import com.example.banking.model.entity.Account;
import com.example.banking.model.entity.User;
import com.example.banking.service.AccountService;
import com.example.banking.service.OperationsConsoleListener;
import com.example.banking.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class AccountOperationHandler {

    private final AccountService accountService;
    private final UserService userService;
    private final OperationsConsoleListener consoleListener;
    private final ConsoleMenu consoleMenu;
    private final ConsoleErrorHandler errorHandler;

    public AccountOperationHandler(AccountService accountService,
                                   UserService userService,
                                   OperationsConsoleListener consoleListener,
                                   ConsoleMenu consoleMenu,
                                   ConsoleErrorHandler errorHandler) {
        this.accountService = accountService;
        this.userService = userService;
        this.consoleListener = consoleListener;
        this.consoleMenu = consoleMenu;
        this.errorHandler = errorHandler;
    }

    public void createNewAccount() {
        String userId = consoleListener.readLine("Enter the user id for which to create an account: ");

        if (!userService.userExists(userId)) {
            consoleMenu.displayError("User with entered id not found!");
            return;
        }

        String login = userService.findUserById(userId)
                .map(User::getLogin)
                .orElse("Unknown");

        try {
            Account account = accountService.createAccountForUser(userId);

            consoleMenu.displaySuccess("New account created with ID: %s for user: %s",
                    account.getId(), login);
        } catch (Exception e) {
            consoleMenu.displayError("Failed to create account: %s", e.getMessage());
        }
    }

    public void closeAccount() {
        String accountId = consoleListener.readLine("Enter account ID: ");
        try {
            accountService.closeAccount(accountId, null);
        } catch (BankingException e) {
            errorHandler.handleBankingException(e);
        } catch (Exception e) {
            consoleMenu.displayError("Unexpected error: %s", e.getMessage());
        }
    }

    public void deposit() {
        consoleMenu.displaySectionHeader("DEPOSIT");
        String accountId = consoleListener.readLine("Enter account ID: ");
        double amount = consoleListener.readDouble("Enter amount to deposit: ");

        try {
            accountService.deposit(accountId, amount);
        } catch (BankingException e) {
            errorHandler.handleBankingException(e);
        } catch (Exception e) {
            consoleMenu.displayError("Unexpected error: %s", e.getMessage());
        }
    }

    public void withdraw() {
        consoleMenu.displaySectionHeader("WITHDRAW");
        String accountId = consoleListener.readLine("Enter account ID to withdraw from: ");
        double amount = consoleListener.readDouble("Enter amount to withdraw: ");

        try {
            accountService.withdraw(accountId, amount);
        } catch (BankingException e) {
            errorHandler.handleBankingException(e);
        } catch (Exception e) {
            consoleMenu.displayError("Unexpected error: %s", e.getMessage());
        }
    }

    public void transfer() {
        consoleMenu.displaySectionHeader("TRANSFER");

        String sourceId = consoleListener.readLine("Enter source account ID: ");
        String targetId = consoleListener.readLine("Enter target account ID: ");
        double amount = consoleListener.readDouble("Enter amount to transfer: ");

        try {
            accountService.transfer(sourceId, targetId, amount);
            consoleMenu.displaySuccess("Transfer completed successfully");

        } catch (BankingException e) {
            // Ошибка уже обработана, но добавим пустую строку
            System.out.println();
            errorHandler.handleBankingException(e);

        } catch (Exception e) {
            System.err.println();
            System.err.printf("❌ Unexpected error: %s%n", e.getMessage());
        }
    }
}