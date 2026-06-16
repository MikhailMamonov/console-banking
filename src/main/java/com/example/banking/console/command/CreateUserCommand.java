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
public class CreateUserCommand extends BaseCommand {

    private final UserService userService;
    private final AccountService accountService;

    public CreateUserCommand(ConsoleInputService consoleInput,
                             UserService userService,
                             AccountService accountService) {
        super(consoleInput);
        this.userService = userService;
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        printSeparator();
        printInfo("Creating new user");

        String login = readLine("Enter login for new user: ");

        if (userService.isLoginExists(login)) {
            printError("User with login '" + login + "' already exists!");
            return;
        }

        try {
            User user = userService.createUser(login);
            Account account = accountService.createAccountForUser(user.getId());

            printSuccess("User created successfully!");
            System.out.printf("   User: %s (ID: %s)%n", user.getLogin(), user.getId());
            System.out.printf("   First account ID: %s with balance: %.2f%n",
                    account.getId(), account.getMoneyAmount());

        } catch (BankingException e) {
            printError("Error creating user: " + e.getMessage());
        } catch (Exception e) {
            printError("Unexpected error: " + e.getMessage());
        }
    }

    @Override
    public ConsoleOperation getOperationType() {
        return ConsoleOperation.USER_CREATE;
    }
}