package com.example.banking.service;

import com.example.banking.exception.BankingException;
import com.example.banking.exception.ErrorType;
import com.example.banking.model.entity.Account;
import com.example.banking.model.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.banking.util.ValidationUtils.*;

@Service
public class AccountService extends BaseAccountService {

    @Value("${account.default-amount:0.0}")
    private String defaultAmount;

    @Value("${account.transfer-commission:0.0}")
    private String transferCommission;

    private double getDefaultAmount() {
        try {
            return Double.parseDouble(defaultAmount);
        } catch (NumberFormatException e) {
            errorHandler.handleError("CONFIGURATION",
                    "Invalid default amount format: " + defaultAmount);
            return 0.0;
        }
    }

    private double getTransferCommission() {
        try {
            return Double.parseDouble(transferCommission);
        } catch (NumberFormatException e) {
            errorHandler.handleError("CONFIGURATION",
                    "Invalid transfer commission format: " + transferCommission);
            return 0.0;
        }
    }

    public Account createAccountForUser(String userId) {
        return executeWithErrorHandling("CREATE_ACCOUNT", () -> {
            validateNotEmpty(userId, "User ID", "CREATE_ACCOUNT");
            findUserOrThrow(userId, "CREATE_ACCOUNT");
            return createAccount(userId, null);
        });
    }

    public Account createAccount(String userId, Double moneyAmount) {
        return executeWithErrorHandling("CREATE_ACCOUNT", () -> {
            double balance = (moneyAmount != null) ? moneyAmount : getDefaultAmount();
            Account account = new Account(userId, balance);

            userService.findUserById(userId).ifPresent(user ->
                    user.getAccountList().add(account));

            return account;
        });
    }

    public void showUserAccounts(String userId) {
        executeVoidWithErrorHandling("SHOW_ACCOUNTS", () -> {
            User user = findUserOrThrow(userId, "SHOW_ACCOUNTS");
            System.out.println("\nАккаунты пользователя " + user.getLogin() + ":");

            if (user.getAccountList().isEmpty()) {
                System.out.println("  No accounts found");
            } else {
                user.getAccountList().forEach(account ->
                        System.out.printf("  ID: %s, Баланс: %.2f%n",
                                account.getId(), account.getMoneyAmount()));
            }
        });
    }

    public void deposit(String accountId, double amount) {
        executeVoidWithErrorHandling("DEPOSIT", () -> {
            validateCommonParameters(accountId, amount, "DEPOSIT");
            Account account = findAccountOrThrow(accountId, "DEPOSIT");
            updateBalance(account, amount, "DEPOSIT", "deposited");
        });
    }

    public void withdraw(String accountId, double amount) {
        executeVoidWithErrorHandling("WITHDRAW", () -> {
            validateCommonParameters(accountId, amount, "WITHDRAW");
            Account account = findAccountOrThrow(accountId, "WITHDRAW");

            validateCondition(account.getMoneyAmount() >= amount,
                    String.format("Insufficient funds! Available: %.2f, Required: %.2f",
                            account.getMoneyAmount(), amount), "WITHDRAW");

            updateBalance(account, -amount, "WITHDRAW", "withdrawn");
        });
    }

    public void transfer(String sourceId, String targetId, double amount) {
        executeVoidWithErrorHandling("TRANSFER", () -> {
            validateNotEmpty(sourceId, "Source Account ID", "TRANSFER");
            validateNotEmpty(targetId, "Target Account ID", "TRANSFER");
            validatePositiveAmount(amount, "TRANSFER");

            Account sourceAccount = findAccountOrThrow(sourceId, "TRANSFER");
            Account targetAccount = findAccountOrThrow(targetId, "TRANSFER");

            boolean isSameUser = sourceAccount.getUserId().equals(targetAccount.getUserId());
            double requiredAmount = isSameUser ? amount : amount + getTransferCommission();

            validateCondition(sourceAccount.getMoneyAmount() >= requiredAmount,
                    buildInsufficientFundsMessage(sourceAccount.getMoneyAmount(), requiredAmount, isSameUser),
                    "TRANSFER");

            // Выполнение перевода
            sourceAccount.setMoneyAmount(sourceAccount.getMoneyAmount() - requiredAmount);
            targetAccount.setMoneyAmount(targetAccount.getMoneyAmount() + amount);

            String commissionInfo = !isSameUser ? String.format(" (commission: %.2f)", getTransferCommission()) : "";
            System.out.printf("Amount %.2f transferred from account %s to account %s.%s%n",
                    amount, sourceId, targetId, commissionInfo);
        });
    }

    public void closeAccount(String accountId, String targetAccountId) {
        executeVoidWithErrorHandling("CLOSE_ACCOUNT", () -> {
            validateNotEmpty(accountId, "Account ID", "CLOSE_ACCOUNT");

            Account accountToClose = findAccountOrThrow(accountId, "CLOSE_ACCOUNT");
            User user = findUserOrThrow(accountToClose.getUserId(), "CLOSE_ACCOUNT");
            List<Account> userAccounts = user.getAccountList();

            // Валидации
            validateCondition(userAccounts.size() > 1,
                    "Cannot close the only account. User must have at least one account.",
                    "CLOSE_ACCOUNT");

            validateCondition(accountToClose.getMoneyAmount() >= 0,
                    String.format("Cannot close account with negative balance: %.2f",
                            accountToClose.getMoneyAmount()), "CLOSE_ACCOUNT");

            // Поиск целевого счета
            Account targetAccount = findTargetAccount(userAccounts, accountId, targetAccountId, "CLOSE_ACCOUNT");

            // Перевод средств
            transferFunds(accountToClose, targetAccount);

            // Удаление счета
            boolean removed = userAccounts.removeIf(acc -> acc.getId().equals(accountId));
            validateCondition(removed, "Failed to remove account " + accountId, "CLOSE_ACCOUNT");

            System.out.printf("Account %s successfully closed. Funds transferred to account %s%n",
                    accountId, targetAccount.getId());
        });
    }

    private Account findTargetAccount(List<Account> accounts, String accountIdToClose,
                                      String preferredTargetId, String operationType) {
        if (preferredTargetId != null && !preferredTargetId.trim().isEmpty()) {
            return accounts.stream()
                    .filter(acc -> acc.getId().equals(preferredTargetId))
                    .findFirst()
                    .orElseThrow(() -> new BankingException(operationType, ErrorType.NOT_FOUND,
                            String.format("Target account %s not found", preferredTargetId)));
        }

        return accounts.stream()
                .filter(acc -> !acc.getId().equals(accountIdToClose))
                .findFirst()
                .orElseThrow(() -> new BankingException(operationType, ErrorType.VALIDATION_ERROR,
                        "No target account available for transfer"));
    }

    private void transferFunds(Account source, Account target) {
        double amount = source.getMoneyAmount();
        if (amount > 0) {
            double newBalance = target.getMoneyAmount() + amount;
            target.setMoneyAmount(newBalance);
            source.setMoneyAmount(0.0);
            System.out.printf("Transferred %.2f from account %s to account %s%n",
                    amount, source.getId(), target.getId());
        }
    }

    private String buildInsufficientFundsMessage(double available, double required, boolean isSameUser) {
        String message = String.format("Insufficient funds! Available: %.2f, Required: %.2f",
                available, required);
        if (!isSameUser) {
            message += String.format(" (including commission: %.2f)", getTransferCommission());
        }
        return message;
    }
}