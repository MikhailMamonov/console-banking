package com.example.banking.console.command;

import com.example.banking.console.ConsoleOperation;
import com.example.banking.exception.BankingException;
import com.example.banking.service.AccountService;
import com.example.banking.service.ConsoleInputService;
import org.springframework.stereotype.Component;

@Component
public class WithdrawCommand extends BaseCommand {

    private final AccountService accountService;

    public WithdrawCommand(ConsoleInputService consoleInput,
                           AccountService accountService) {
        super(consoleInput);
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        printSeparator();
        printInfo("Withdraw from account");

        String accountId = readLine("Enter account ID: ");
        double amount = readDouble("Enter amount to withdraw: ");

        try {
            accountService.withdraw(accountId, amount);
            printSuccess(String.format("Withdrawal of %.2f completed successfully!", amount));
        } catch (BankingException e) {
            printError("Error making withdrawal: " + e.getMessage());
        } catch (Exception e) {
            printError("Unexpected error: " + e.getMessage());
        }
    }

    @Override
    public ConsoleOperation getOperationType() {
        return ConsoleOperation.ACCOUNT_WITHDRAW;
    }
}