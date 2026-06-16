package com.example.banking.service;

import com.example.banking.exception.BankingException;
import com.example.banking.exception.ErrorType;
import com.example.banking.model.entity.Account;
import com.example.banking.model.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of AccountService with built-in validation.
 */
@Service
public class AccountServiceImpl implements AccountService {

    private static final double DEFAULT_BALANCE_ON_ERROR = 0.0;

    @Value("${account.default-amount:0.0}")
    private String defaultAmount;

    @Value("${account.transfer-commission:0.0}")
    private String transferCommission;

    private final UserService userService;
    private final IdGeneratorService idGeneratorService;
    private final AccountTransactionProcessor transactionProcessor;
    private final AccountLogger accountLogger;

    public AccountServiceImpl(UserService userService,
                              IdGeneratorService idGeneratorService,
                              AccountTransactionProcessor transactionProcessor,
                              AccountLogger accountLogger) {
        this.userService = userService;
        this.idGeneratorService = idGeneratorService;
        this.transactionProcessor = transactionProcessor;
        this.accountLogger = accountLogger;
    }

    // === Validation Methods ===

    private void validateNotEmpty(String value, String fieldName, String operationType) {
        if (value == null || value.trim().isEmpty()) {
            throw new BankingException(operationType, ErrorType.VALIDATION_ERROR,
                    String.format("%s cannot be empty", fieldName));
        }
    }

    private void validatePositiveAmount(double amount, String operationType) {
        if (amount <= 0) {
            throw new BankingException(operationType, ErrorType.VALIDATION_ERROR,
                    String.format("Amount must be positive: %.2f", amount));
        }
    }

    private void validateTransaction(String accountId, double amount, String operationType) {
        validateNotEmpty(accountId, "Account ID", operationType);
        validatePositiveAmount(amount, operationType);
    }

    private void validateUser(String userId, String operationType) {
        validateNotEmpty(userId, "User ID", operationType);
    }

    private void validateTransfer(String sourceId, String targetId, double amount) {
        validateNotEmpty(sourceId, "Source Account ID", "TRANSFER");
        validateNotEmpty(targetId, "Target Account ID", "TRANSFER");
        validatePositiveAmount(amount, "TRANSFER");
    }

    private void validateCondition(boolean condition, String message, String operationType) {
        if (!condition) {
            throw new BankingException(operationType, ErrorType.VALIDATION_ERROR, message);
        }
    }

    // === Helper Methods ===

    private double getDefaultAmount() {
        try {
            return Double.parseDouble(defaultAmount);
        } catch (NumberFormatException e) {
            return DEFAULT_BALANCE_ON_ERROR;
        }
    }

    private double getTransferCommission() {
        try {
            return Double.parseDouble(transferCommission);
        } catch (NumberFormatException e) {
            return DEFAULT_BALANCE_ON_ERROR;
        }
    }

    private User findUserOrThrow(String userId) {
        return userService.findUserOrThrow(userId);
    }

    private Account findAccountOrThrow(String accountId) {
        return findAccountById(accountId)
                .orElseThrow(() -> new BankingException("ACCOUNT_OPERATION", ErrorType.ACCOUNT_NOT_FOUND,
                        String.format("Account with ID %s not found", accountId)));
    }

    private Optional<Account> findAccountById(String accountId) {
        return userService.getAllUsers().stream()
                .flatMap(user -> user.getAccountList().stream())
                .filter(account -> account.getId().equals(accountId))
                .findFirst();
    }

    private void validateSufficientFunds(Account account, double requiredAmount, double commission) {
        if (account.getMoneyAmount() < requiredAmount) {
            String message = buildInsufficientFundsMessage(account.getMoneyAmount(), requiredAmount, commission);
            throw new BankingException("TRANSFER", ErrorType.INSUFFICIENT_FUNDS, message);
        }
    }

    private String buildInsufficientFundsMessage(double available, double required, double commission) {
        if (commission > 0) {
            return String.format("Insufficient funds! Available: %.2f, Required: %.2f (amount: %.2f + commission: %.2f)",
                    available, required, required - commission, commission);
        }
        return String.format("Insufficient funds! Available: %.2f, Required: %.2f", available, required);
    }

    private Account findTargetAccount(List<Account> accounts, String accountIdToClose, String preferredTargetId) {
        if (preferredTargetId != null && !preferredTargetId.trim().isEmpty()) {
            if (preferredTargetId.equals(accountIdToClose)) {
                throw new BankingException("CLOSE_ACCOUNT", ErrorType.VALIDATION_ERROR,
                        "Cannot transfer funds to the same account being closed");
            }
            return accounts.stream()
                    .filter(acc -> acc.getId().equals(preferredTargetId))
                    .findFirst()
                    .orElseThrow(() -> new BankingException("CLOSE_ACCOUNT", ErrorType.ACCOUNT_NOT_FOUND,
                            String.format("Target account %s not found", preferredTargetId)));
        }
        return accounts.stream()
                .filter(acc -> !acc.getId().equals(accountIdToClose))
                .findFirst()
                .orElseThrow(() -> new BankingException("CLOSE_ACCOUNT", ErrorType.ACCOUNT_NOT_FOUND,
                        "No target account available"));
    }

    private void transferFunds(Account source, Account target) {
        double amount = source.getMoneyAmount();
        if (amount > DEFAULT_BALANCE_ON_ERROR) {
            target.setMoneyAmount(target.getMoneyAmount() + amount);
            source.setMoneyAmount(0.0);
            accountLogger.logFundsTransfer(amount, source.getId(), target.getId());
        }
    }

    // === Public Methods ===

    @Override
    public Account createAccountForUser(String userId) {
        validateUser(userId, "CREATE_ACCOUNT_FOR_USER");

        User user = findUserOrThrow(userId);
        Account account = createAccount(userId, null);
        user.getAccountList().add(account);
        accountLogger.logAccountCreation(account);
        return account;
    }

    @Override
    public Account createAccount(String userId, Double moneyAmount) {
        validateUser(userId, "CREATE_ACCOUNT");

        double balance = (moneyAmount != null) ? moneyAmount : getDefaultAmount();
        String accountId = idGeneratorService.generateAccountId();
        return new Account(accountId, userId, balance);
    }

    @Override
    public void showUserAccounts(String userId) {
        validateUser(userId, "SHOW_USER_ACCOUNTS");

        User user = findUserOrThrow(userId);
        accountLogger.logUserAccounts(user);
    }

    @Override
    public void deposit(String accountId, double amount) {
        validateTransaction(accountId, amount, "DEPOSIT");

        Account account = findAccountOrThrow(accountId);
        transactionProcessor.processDeposit(account, amount);
        accountLogger.logDeposit(accountId, amount);
    }

    @Override
    public void withdraw(String accountId, double amount) {
        validateTransaction(accountId, amount, "WITHDRAW");

        Account account = findAccountOrThrow(accountId);
        validateCondition(account.getMoneyAmount() >= amount,
                String.format("Insufficient funds! Available: %.2f, Required: %.2f",
                        account.getMoneyAmount(), amount), "WITHDRAW");
        transactionProcessor.processWithdrawal(account, amount);
        accountLogger.logWithdrawal(accountId, amount);
    }

    @Override
    public void transfer(String sourceId, String targetId, double amount) {
        validateTransfer(sourceId, targetId, amount);

        Account sourceAccount = findAccountOrThrow(sourceId);
        Account targetAccount = findAccountOrThrow(targetId);
        boolean isSameUser = sourceAccount.getUserId().equals(targetAccount.getUserId());
        double commission = isSameUser ? 0.0 : getTransferCommission();
        double totalRequired = amount + commission;

        validateSufficientFunds(sourceAccount, totalRequired, commission);
        transactionProcessor.processTransfer(sourceAccount, targetAccount, amount, commission);
        accountLogger.logTransfer(sourceId, targetId, amount, commission);
    }

    @Override
    public void closeAccount(String accountId, String targetAccountId) {
        validateUser(accountId, "CLOSE_ACCOUNT");

        Account accountToClose = findAccountOrThrow(accountId);
        User user = findUserOrThrow(accountToClose.getUserId());
        List<Account> userAccounts = user.getAccountList();

        validateCondition(userAccounts.size() > 1,
                "Cannot close the only account. User must have at least one account.",
                "CLOSE_ACCOUNT");

        validateCondition(accountToClose.getMoneyAmount() >= 0,
                String.format("Cannot close account with negative balance: %.2f",
                        accountToClose.getMoneyAmount()), "CLOSE_ACCOUNT");

        Account targetAccount = findTargetAccount(userAccounts, accountId, targetAccountId);
        transferFunds(accountToClose, targetAccount);

        boolean removed = userAccounts.removeIf(acc -> acc.getId().equals(accountId));
        validateCondition(removed, "Failed to remove account " + accountId, "CLOSE_ACCOUNT");

        accountLogger.logAccountClosure(accountId, targetAccount.getId());
    }
}