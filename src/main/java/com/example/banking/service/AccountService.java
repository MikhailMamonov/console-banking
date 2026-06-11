package com.example.banking.service;

import com.example.banking.model.entity.Account;
import com.example.banking.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private UserService userService;

    public Account createAccount(String id, String userId, double moneyAmount) {
        Account account = new Account(id, userId, moneyAmount);

        userService.findUserById(userId).ifPresent(user -> {
            user.getAccountList().add(account);
            System.out.println("Аккаунт " + id + " привязан к пользователю " + user.getLogin());
        });

        return account;
    }

    public void showUserAccounts(String userId){
        userService.findUserById(userId).ifPresent(user -> {
            System.out.println("\nАккаунты пользователя " + user.getLogin() + ":");
            user.getAccountList().forEach(account ->   System.out.println("  ID: " + account.getId() +
                                                            ", Баланс: " + account.getMoneyAmount()));
        });

    }

    public void deposit(String accountId, double amount) {
        findAccountById(accountId).ifPresent(account -> {
            double newBalance = account.getMoneyAmount() + amount;
            account.setMoneyAmount(newBalance);
            System.out.println("Счет пополнен. Новый баланс: " + newBalance);
        });
    }

    private Optional<Account> findAccountById(String accountId) {
        return userService.getAllUsers().stream()
                .flatMap(user -> user.getAccountList().stream())
                .filter(account -> account.getId().equals(accountId))
                .findFirst();
    }

    public boolean closeAccount(String accountId) {

        if (accountId == null || accountId.trim().isEmpty()) {
            System.out.println("Invalid account ID: "+ accountId);
            return false;
        }
        Optional<Account> optionalAccount = findAccountById(accountId);
        if (optionalAccount.isEmpty()) {
            System.out.println("Account not found ID: "+ accountId);
            return false;
        }

        Account account = optionalAccount.get();

        Optional<User> optionalUser = userService.findUserById(account.getUserId());
        if (optionalUser.isEmpty()) {
            System.out.println("User not found for account: "+ accountId);
            return false;
        }

        User user = optionalUser.get();
        List<Account> accountList = user.getAccountList();

        // 4. Проверка количества счетов
        if (accountList.size() <= 1) {
            System.out.println("Cannot close the only account. User must have at least one account.");
            return false;
        }

        // 5. Проверка баланса
        if (account.getMoneyAmount() < 0) {
            System.out.printf("Cannot close account with negative balance: %.2f%n", account.getMoneyAmount());
            return false;
        }

        // 6. Находим целевой счет (первый, не закрываемый)
        Account targetAccount = null;
        for (Account acc : accountList) {
            if (!acc.getId().equals(accountId)) {
                targetAccount = acc;
                break;
            }
        }

        if (targetAccount == null) {
            System.out.println("Target account not found despite size > 1 for user: "+ user.getId());
            return false;
        }

        // 7. Перевод средств
        double balanceToTransfer = account.getMoneyAmount();
        if (balanceToTransfer > 0) {
            double newBalance = targetAccount.getMoneyAmount() + balanceToTransfer;
            targetAccount.setMoneyAmount(newBalance);
            System.out.printf("Transferred %.2f from account %s to account %s%n",
                    balanceToTransfer, accountId, targetAccount.getId());
        }

        // 8. Удаление счета
        boolean removed = accountList.removeIf(item -> item.getId().equals(accountId));

        if (!removed) {
            System.out.printf("Failed to remove account %s from user %s", accountId, user.getId());
            return false;
        }

        return true;
    }
}
