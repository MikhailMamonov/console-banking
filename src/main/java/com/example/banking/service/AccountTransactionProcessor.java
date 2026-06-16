package com.example.banking.service;

import com.example.banking.model.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountTransactionProcessor {

    public void processDeposit(Account account, double amount) {
        double newBalance = account.getMoneyAmount() + amount;
        account.setMoneyAmount(newBalance);
    }

    public void processWithdrawal(Account account, double amount) {
        double newBalance = account.getMoneyAmount() - amount;
        account.setMoneyAmount(newBalance);
    }

    public void processTransfer(Account source, Account target, double amount, double commission) {
        double totalDebit = amount + commission;
        source.setMoneyAmount(source.getMoneyAmount() - totalDebit);
        target.setMoneyAmount(target.getMoneyAmount() + amount);
    }
}