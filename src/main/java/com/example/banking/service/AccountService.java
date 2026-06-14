package com.example.banking.service;

import com.example.banking.exception.BankingException;
import com.example.banking.exception.ErrorHandler;
import com.example.banking.exception.ErrorType;
import com.example.banking.model.entity.Account;
import com.example.banking.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private UserService userService;

    @Autowired
    private ErrorHandler errorHandler;

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
        try {
            validateUserId(userId);

            User user = userService.findUserById(userId)
                    .orElseThrow(() -> new BankingException("CREATE_ACCOUNT", ErrorType.USER_NOT_FOUND,
                            "User with ID '" + userId + "' not found!"));

            return createAccount(userId, null);

        } catch (BankingException e) {
            errorHandler.handleError(e);
            return null;
        } catch (Exception e) {
            errorHandler.handleError("CREATE_ACCOUNT", "Unexpected error: " + e.getMessage());
            return null;
        }
    }

    public Account createAccount(String userId, Double moneyAmount) {
        try {
            double balance = (moneyAmount != null) ? moneyAmount : getDefaultAmount();
            Account account = new Account(userId, balance);

            userService.findUserById(userId).ifPresent(user -> {
                user.getAccountList().add(account);
            });

            return account;

        } catch (Exception e) {
            errorHandler.handleError("CREATE_ACCOUNT", "Failed to create account: " + e.getMessage());
            return null;
        }
    }

    public void showUserAccounts(String userId) {
        try {
            Optional<User> userOptional = userService.findUserById(userId);

            if (userOptional.isEmpty()) {
                errorHandler.handleError("SHOW_ACCOUNTS", "User with ID '" + userId + "' not found!");
                return;
            }

            User user = userOptional.get();
            System.out.println("\nАккаунты пользователя " + user.getLogin() + ":");

            if (user.getAccountList().isEmpty()) {
                System.out.println("  No accounts found");
            } else {
                user.getAccountList().forEach(account ->
                        System.out.println("  ID: " + account.getId() +
                                ", Баланс: " + account.getMoneyAmount()));
            }

        } catch (Exception e) {
            errorHandler.handleError("SHOW_ACCOUNTS", "Failed to show accounts: " + e.getMessage());
        }
    }

    public void deposit(String accountId, double amount) {
        try {
            if (accountId == null || accountId.trim().isEmpty()) {
                errorHandler.handleError("DEPOSIT", "Account ID cannot be empty!");
                return;
            }

            if (amount <= 0) {
                errorHandler.handleError("DEPOSIT",
                        String.format("Deposit amount must be positive! Current amount: %.2f", amount));
                return;
            }

            Optional<Account> accountOptional = findAccountById(accountId);

            if (accountOptional.isEmpty()) {
                errorHandler.handleError("DEPOSIT",
                        String.format("Account with ID '%s' not found!", accountId));
                return;
            }

            Account account = accountOptional.get();
            double newBalance = account.getMoneyAmount() + amount;
            account.setMoneyAmount(newBalance);

            System.out.println(String.format("Amount %.2f deposited to account ID: %s. New balance: %.2f",
                    amount, accountId, newBalance));

        } catch (Exception e) {
            errorHandler.handleError("DEPOSIT", "Failed to deposit: " + e.getMessage());
        }
    }

    public void transfer(String source, String target, double amount) {
        try {
            if (source == null || source.trim().isEmpty()) {
                errorHandler.handleError("TRANSFER", "Source Account ID cannot be empty!");
                return;
            }
            if (target == null || target.trim().isEmpty()) {
                errorHandler.handleError("TRANSFER", "Target Account ID cannot be empty!");
                return;
            }
            if (amount <= 0) {
                errorHandler.handleError("TRANSFER",
                        String.format("Transfer amount must be positive! Current amount: %.2f", amount));
                return;
            }

            Optional<Account> sourceAccountOpt = findAccountById(source);
            if (sourceAccountOpt.isEmpty()) {
                errorHandler.handleError("TRANSFER",
                        String.format("Source Account with ID '%s' not found!", source));
                return;
            }

            Optional<Account> targetAccountOpt = findAccountById(target);
            if (targetAccountOpt.isEmpty()) {
                errorHandler.handleError("TRANSFER",
                        String.format("Target Account with ID '%s' not found!", target));
                return;
            }

            Account sourceAccount = sourceAccountOpt.get();
            Account targetAccount = targetAccountOpt.get();

            boolean isSameUser = sourceAccount.getUserId().equals(targetAccount.getUserId());
            double requiredAmount = isSameUser ? amount : amount + getTransferCommission();

            if (sourceAccount.getMoneyAmount() < requiredAmount) {
                String message = String.format(
                        "Insufficient funds! Available: %.2f, Required: %.2f",
                        sourceAccount.getMoneyAmount(), requiredAmount);
                if (!isSameUser) {
                    message += String.format(" (including commission: %.2f)", getTransferCommission());
                }
                errorHandler.handleError("TRANSFER", message);
                return;
            }

            sourceAccount.setMoneyAmount(sourceAccount.getMoneyAmount() - requiredAmount);
            targetAccount.setMoneyAmount(targetAccount.getMoneyAmount() + amount);

            String commissionInfo = !isSameUser ?
                    String.format(" (commission: %.2f)", getTransferCommission()) : "";
            System.out.println(String.format("Amount %.2f transferred from account ID %s to account ID %s.%s",
                    amount, source, target, commissionInfo));

        } catch (Exception e) {
            errorHandler.handleError("TRANSFER", "Unexpected error during transfer: " + e.getMessage());
        }
    }

    public void withdraw(String source, double amount) {
        try {
            if (source == null || source.trim().isEmpty()) {
                errorHandler.handleError("WITHDRAW", "Account ID cannot be empty!");
                return;
            }

            if (amount <= 0) {
                errorHandler.handleError("WITHDRAW",
                        String.format("Withdraw amount must be positive! Current amount: %.2f", amount));
                return;
            }

            Optional<Account> accountOptional = findAccountById(source);
            if (accountOptional.isEmpty()) {
                errorHandler.handleError("WITHDRAW",
                        String.format("Account with ID '%s' not found!", source));
                return;
            }

            Account sourceAccount = accountOptional.get();

            if (sourceAccount.getMoneyAmount() < amount) {
                errorHandler.handleError("WITHDRAW",
                        String.format("Insufficient funds! Available: %.2f, Required: %.2f",
                                sourceAccount.getMoneyAmount(), amount));
                return;
            }

            sourceAccount.setMoneyAmount(sourceAccount.getMoneyAmount() - amount);

            System.out.println(String.format("Amount %.2f withdrawn from account ID %s. New balance: %.2f",
                    amount, source, sourceAccount.getMoneyAmount()));

        } catch (Exception e) {
            errorHandler.handleError("WITHDRAW", "Unexpected error during withdraw: " + e.getMessage());
        }
    }

    public void closeAccount(String accountId, String targetAccountId) {
        try {
            if (accountId == null || accountId.trim().isEmpty()) {
                errorHandler.handleError("CLOSE_ACCOUNT", "Account ID cannot be empty!");
                return;
            }

            Optional<Account> accountToCloseOpt = findAccountById(accountId);
            if (accountToCloseOpt.isEmpty()) {
                errorHandler.handleError("CLOSE_ACCOUNT",
                        String.format("Account with ID '%s' not found!", accountId));
                return;
            }

            Account accountToClose = accountToCloseOpt.get();

            Optional<User> userOpt = userService.findUserById(accountToClose.getUserId());
            if (userOpt.isEmpty()) {
                errorHandler.handleError("CLOSE_ACCOUNT",
                        "User not found for account: " + accountId);
                return;
            }

            User user = userOpt.get();
            List<Account> userAccounts = user.getAccountList();

            if (userAccounts.size() <= 1) {
                errorHandler.handleError("CLOSE_ACCOUNT",
                        "Cannot close the only account. User must have at least one account.");
                return;
            }

            if (accountToClose.getMoneyAmount() < 0) {
                errorHandler.handleError("CLOSE_ACCOUNT",
                        String.format("Cannot close account with negative balance: %.2f",
                                accountToClose.getMoneyAmount()));
                return;
            }

            Account targetAccount;
            if (targetAccountId != null && !targetAccountId.trim().isEmpty()) {
                Optional<Account> targetOpt = findAccountById(targetAccountId);
                if (targetOpt.isEmpty()) {
                    errorHandler.handleError("CLOSE_ACCOUNT",
                            String.format("Target account %s not found", targetAccountId));
                    return;
                }
                targetAccount = targetOpt.get();
            } else {
                targetAccount = userAccounts.stream()
                        .filter(acc -> !acc.getId().equals(accountId))
                        .findFirst()
                        .orElse(null);

                if (targetAccount == null) {
                    errorHandler.handleError("CLOSE_ACCOUNT",
                            "No target account available for transfer");
                    return;
                }
            }

            double amountToTransfer = accountToClose.getMoneyAmount();
            if (amountToTransfer > 0) {
                double newBalance = targetAccount.getMoneyAmount() + amountToTransfer;
                targetAccount.setMoneyAmount(newBalance);
                accountToClose.setMoneyAmount(0.0);
                System.out.printf("Transferred %.2f from account %s to account %s%n",
                        amountToTransfer, accountId, targetAccount.getId());
            }

            boolean removed = userAccounts.removeIf(acc -> acc.getId().equals(accountId));
            if (!removed) {
                errorHandler.handleError("CLOSE_ACCOUNT",
                        String.format("Failed to remove account %s from user %s", accountId, user.getId()));
                return;
            }

            System.out.printf("Account %s successfully closed. Funds transferred to account %s%n",
                    accountId, targetAccount.getId());

        } catch (Exception e) {
            errorHandler.handleError("CLOSE_ACCOUNT", "Unexpected error during account closure: " + e.getMessage());
        }
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new BankingException("VALIDATION", ErrorType.VALIDATION_ERROR,
                    "User ID cannot be empty!");
        }
    }

    private Optional<Account> findAccountById(String accountId) {
        return userService.getAllUsers().stream()
                .flatMap(user -> user.getAccountList().stream())
                .filter(account -> account.getId().equals(accountId))
                .findFirst();
    }
}