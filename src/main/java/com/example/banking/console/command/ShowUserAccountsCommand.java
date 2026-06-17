package com.example.banking.console.command;

import com.example.banking.console.ConsoleOperation;
import com.example.banking.exception.BankingException;
import com.example.banking.service.AccountService;
import com.example.banking.service.ConsoleInputService;
import org.springframework.stereotype.Component;

@Component
public class ShowUserAccountsCommand extends BaseCommand {

    private final AccountService accountService;

    public ShowUserAccountsCommand(ConsoleInputService consoleInput,
                                   AccountService accountService) {
        super(consoleInput);
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        printSeparator();
        printInfo("Showing user accounts");

        String userId = readLine("Enter user ID: ");

        try {
            accountService.showUserAccounts(userId);
        } catch (BankingException e) {
            printError("Error: " + e.getMessage());
        } catch (Exception e) {
            printError("Unexpected error: " + e.getMessage());
        }
    }

    @Override
    public ConsoleOperation getOperationType() {
        return ConsoleOperation.SHOW_USER_ACCOUNTS;
    }
}