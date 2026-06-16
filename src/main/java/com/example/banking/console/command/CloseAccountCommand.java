package com.example.banking.console.command;

import com.example.banking.console.ConsoleOperation;
import com.example.banking.exception.BankingException;
import com.example.banking.service.AccountService;
import com.example.banking.service.ConsoleInputService;
import com.example.banking.service.OperationsConsoleListener;
import org.springframework.stereotype.Component;

@Component
public class CloseAccountCommand extends BaseCommand {

    private final AccountService accountService;

    public CloseAccountCommand(ConsoleInputService consoleInput,
                               AccountService accountService) {
        super(consoleInput);
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        printSeparator();
        printInfo("Closing account");

        String accountId = readLine("Enter account ID to close: ");
        String targetAccountId = readLine("Enter target account ID for remaining funds (or press Enter to skip): ");

        try {
            accountService.closeAccount(accountId, targetAccountId.isEmpty() ? null : targetAccountId);
            printSuccess("Account '" + accountId + "' closed successfully!");
        } catch (BankingException e) {
            printError("Error closing account: " + e.getMessage());
        } catch (Exception e) {
            printError("Unexpected error: " + e.getMessage());
        }
    }

    @Override
    public ConsoleOperation getOperationType() {
        return ConsoleOperation.ACCOUNT_CLOSE;
    }
}