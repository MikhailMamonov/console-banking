package com.example.banking.console.handler;

import com.example.banking.console.ConsoleErrorHandler;
import com.example.banking.console.ConsoleMenu;
import com.example.banking.exception.BankingException;
import com.example.banking.model.entity.Account;
import com.example.banking.model.entity.User;
import com.example.banking.service.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserOperationHandler {

    private final UserService userService;
    private final AccountService accountService;
    private final OperationsConsoleListener consoleListener;
    private final ConsoleMenu consoleMenu;
    private final ConsoleErrorHandler errorHandler;
    private final UserAccountService userAccountService;
    private final ConfigService configService;

    public UserOperationHandler(UserService userService,
                                AccountService accountService,
                                OperationsConsoleListener consoleListener,
                                ConsoleMenu consoleMenu,
                                ConsoleErrorHandler errorHandler,
                                UserAccountService userAccountService,
                                ConfigService configService) {
        this.userService = userService;
        this.accountService = accountService;
        this.consoleListener = consoleListener;
        this.consoleMenu = consoleMenu;
        this.errorHandler = errorHandler;
        this.userAccountService = userAccountService;
        this.configService = configService;
    }

    public void createNewUser() {
        String login = consoleListener.readLine("Enter login for new user: ");

        try {
            double defaultBalance = configService.getDefaultAmount();
            User user = userAccountService.createUserWithAccount(login, defaultBalance);

            Account firstAccount = user.getAccountList().get(0);
            consoleMenu.displaySuccess("User created successfully!");
            consoleMenu.displayInfo("User ID: %s", user.getId());
            consoleMenu.displayInfo("Account ID: %s", firstAccount.getId());
            consoleMenu.displayInfo("Initial balance: %.2f", firstAccount.getMoneyAmount());

        } catch (Exception e) {
            consoleMenu.displayError("Unexpected error: %s", e.getMessage());
        }
    }

    public void showAllUsers() {
        userService.showAllUsers();
    }

    public void showUserAccounts() {
        String userId = consoleListener.readLine("Enter user ID: ");
        accountService.showUserAccounts(userId);
    }
}