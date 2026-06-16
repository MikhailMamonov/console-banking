package com.example.banking.console.command;

import com.example.banking.console.ConsoleOperation;
import com.example.banking.exception.BankingException;
import com.example.banking.service.AccountService;
import com.example.banking.service.ConsoleInputService;
import com.example.banking.service.OperationsConsoleListener;
import org.springframework.stereotype.Component;

@Component
public class TransferCommand extends BaseCommand {

    private final AccountService accountService;

    public TransferCommand(ConsoleInputService consoleInput,
                           AccountService accountService) {
        super(consoleInput);
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        printSeparator();
        printInfo("Transfer between accounts");

        String sourceId = readLine("Enter source account ID: ");
        String targetId = readLine("Enter target account ID: ");
        double amount = readDouble("Enter amount to transfer: ");

        try {
            accountService.transfer(sourceId, targetId, amount);
            printSuccess(String.format("Transfer of %.2f completed successfully!", amount));
            System.out.printf("   From: %s%n", sourceId);
            System.out.printf("   To: %s%n", targetId);
        } catch (BankingException e) {
            printError("Error making transfer: " + e.getMessage());
        } catch (Exception e) {
            printError("Unexpected error: " + e.getMessage());
        }
    }

    @Override
    public ConsoleOperation getOperationType() {
        return ConsoleOperation.ACCOUNT_TRANSFER;
    }
}