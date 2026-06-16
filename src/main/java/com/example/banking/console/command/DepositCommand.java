package com.example.banking.console.command;

import com.example.banking.console.ConsoleOperation;
import com.example.banking.exception.BankingException;
import com.example.banking.service.AccountService;
import com.example.banking.service.ConsoleInputService;
import com.example.banking.service.OperationsConsoleListener;
import org.springframework.stereotype.Component;

@Component
public class DepositCommand extends BaseCommand {

    private final AccountService accountService;

    public DepositCommand(ConsoleInputService consoleInput,
                          AccountService accountService) {
        super(consoleInput);
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        printSeparator();
        printInfo("Deposit to account");

        String accountId = readLine("Enter account ID: ");
        double amount = readDouble("Enter amount to deposit: ");

        try {
            accountService.deposit(accountId, amount);
            printSuccess(String.format("Deposit of %.2f completed successfully!", amount));
        } catch (BankingException e) {
            printError("Error making deposit: " + e.getMessage());
        } catch (Exception e) {
            printError("Unexpected error: " + e.getMessage());
        }
    }

    @Override
    public ConsoleOperation getOperationType() {
        return ConsoleOperation.ACCOUNT_DEPOSIT;
    }
}