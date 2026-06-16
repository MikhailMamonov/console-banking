package com.example.banking.console.command;

import com.example.banking.console.ConsoleOperation;
import com.example.banking.exception.BankingException;
import com.example.banking.model.entity.Account;
import com.example.banking.model.entity.User;
import com.example.banking.service.AccountService;
import com.example.banking.service.ConsoleInputService;
import com.example.banking.service.OperationsConsoleListener;
import com.example.banking.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class CreateAccountCommand extends BaseCommand {

    private final AccountService accountService;
    private final UserService userService;

    public CreateAccountCommand(ConsoleInputService consoleInput,
                                AccountService accountService,
                                UserService userService) {
        super(consoleInput);
        this.accountService = accountService;
        this.userService = userService;
    }

    @Override
    public void execute() {
        printSeparator();
        printInfo("Creating new account");

        String userId = readLine("Enter user ID for the new account: ");

        if (!userService.userExists(userId)) {
            printError("User with ID '" + userId + "' not found!");
            return;
        }

        try {
            Account account = accountService.createAccountForUser(userId);
            User user = userService.findUserOrThrow(userId);

            printSuccess("Account created successfully!");
            System.out.printf("   Account ID: %s%n", account.getId());
            System.out.printf("   User: %s (ID: %s)%n", user.getLogin(), user.getId());
            System.out.printf("   Initial balance: %.2f%n", account.getMoneyAmount());

        } catch (BankingException e) {
            printError("Error creating account: " + e.getMessage());
        } catch (Exception e) {
            printError("Unexpected error: " + e.getMessage());
        }
    }

    @Override
    public ConsoleOperation getOperationType() {
        return ConsoleOperation.ACCOUNT_CREATE;
    }
}