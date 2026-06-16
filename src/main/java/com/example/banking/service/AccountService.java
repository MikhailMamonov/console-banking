package com.example.banking.service;

import com.example.banking.exception.ErrorHandler;
import com.example.banking.model.entity.Account;
import com.example.banking.model.entity.User;
import com.example.banking.exception.BankingException;
import com.example.banking.exception.ErrorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;  // ← ДОБАВИТЬ ЭТОТ ИМПОРТ

@Service
public class AccountService {

    private static final double DEFAULT_BALANCE_ON_ERROR = 0.0;
    private static final String CONFIGURATION_ERROR_CONTEXT = "CONFIGURATION";
    private static final String CREATE_ACCOUNT_CONTEXT = "CREATE_ACCOUNT";
    private static final String DEPOSIT_CONTEXT = "DEPOSIT";
    private static final String WITHDRAW_CONTEXT = "WITHDRAW";
    private static final String TRANSFER_CONTEXT = "TRANSFER";
    private static final String CLOSE_ACCOUNT_CONTEXT = "CLOSE_ACCOUNT";
    private static final String SHOW_ACCOUNTS_CONTEXT = "SHOW_ACCOUNTS";

    @Autowired
    private UserService userService;

    @Value("${account.default-amount:0.0}")
    private String defaultAmount;

    @Value("${account.transfer-commission:0.0}")
    private String transferCommission;

    private final IdGeneratorService idGeneratorService;
    private final AccountTransactionProcessor transactionProcessor;
    private final AccountLogger accountLogger;
    private final ErrorHandler errorHandler;

    public AccountService(AccountTransactionProcessor transactionProcessor,
                          AccountLogger accountLogger,
                          IdGeneratorService idGeneratorService,
                          ErrorHandler errorHandler) {
        this.transactionProcessor = transactionProcessor;
        this.accountLogger = accountLogger;
        this.idGeneratorService = idGeneratorService;
        this.errorHandler = errorHandler;
    }

    // === Вспомогательные методы ===

    private double getDefaultAmount() {
        try {
            return Double.parseDouble(defaultAmount);
        } catch (NumberFormatException e) {
            errorHandler.handleError(CONFIGURATION_ERROR_CONTEXT,
                    "Invalid default amount format: " + defaultAmount);
            return DEFAULT_BALANCE_ON_ERROR;
        }
    }

    private double getTransferCommission() {
        try {
            return Double.parseDouble(transferCommission);
        } catch (NumberFormatException e) {
            errorHandler.handleError(CONFIGURATION_ERROR_CONTEXT,
                    "Invalid transfer commission format: " + transferCommission);
            return DEFAULT_BALANCE_ON_ERROR;
        }
    }

    private User findUserOrThrow(String userId, String operationType) {
        return userService.findUserById(userId)
                .orElseThrow(() -> new BankingException(operationType, ErrorType.USER_NOT_FOUND,
                        String.format("User with ID %s not found", userId)));
    }

    private Account findAccountOrThrow(String accountId, String operationType) {
        return findAccountById(accountId)
                .orElseThrow(() -> new BankingException(operationType, ErrorType.ACCOUNT_NOT_FOUND,
                        String.format("Account with ID %s not found", accountId)));
    }

    private Optional<Account> findAccountById(String accountId) {
        return userService.getAllUsers().stream()
                .flatMap(user -> user.getAccountList().stream())
                .filter(account -> account.getId().equals(accountId))
                .findFirst();
    }

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

    private void validateCommonParameters(String accountId, double amount, String operationType) {
        validateNotEmpty(accountId, "Account ID", operationType);
        validatePositiveAmount(amount, operationType);
    }

    private void validateCondition(boolean condition, String message, String operationType) {
        if (!condition) {
            throw new BankingException(operationType, ErrorType.VALIDATION_ERROR, message);
        }
    }

    private void validateSufficientFunds(Account account, double requiredAmount, double commission) {
        if (account.getMoneyAmount() < requiredAmount) {
            String message = buildInsufficientFundsMessage(account.getMoneyAmount(), requiredAmount, commission);
            throw new BankingException(TRANSFER_CONTEXT, ErrorType.INSUFFICIENT_FUNDS, message);
        }
    }

    private String buildInsufficientFundsMessage(double available, double required, double commission) {
        if (commission > 0) {
            return String.format("Insufficient funds! Available: %.2f, Required: %.2f (amount: %.2f + commission: %.2f)",
                    available, required, required - commission, commission);
        }
        return String.format("Insufficient funds! Available: %.2f, Required: %.2f", available, required);
    }

    private <T> T executeWithErrorHandling(String operationType, Supplier<T> action) {
        try {
            return action.get();
        } catch (BankingException e) {
            throw e;
        } catch (Exception e) {
            errorHandler.handleError(operationType, "Unexpected error: " + e.getMessage());
            throw new BankingException(operationType, ErrorType.SYSTEM_ERROR, e.getMessage());
        }
    }

    private void executeVoidWithErrorHandling(String operationType, Runnable action) {
        try {
            action.run();
        } catch (BankingException e) {
            throw e;
        } catch (Exception e) {
            errorHandler.handleError(operationType, "Unexpected error: " + e.getMessage());
            throw new BankingException(operationType, ErrorType.SYSTEM_ERROR, e.getMessage());
        }
    }

    // === Публичные методы ===

    public Account createAccountForUser(String userId) {
        return executeWithErrorHandling(CREATE_ACCOUNT_CONTEXT, () -> {
            validateNotEmpty(userId, "User ID", CREATE_ACCOUNT_CONTEXT);
            User user = findUserOrThrow(userId, CREATE_ACCOUNT_CONTEXT);
            Account account = createAccount(userId, null);
            user.getAccountList().add(account);
            accountLogger.logAccountCreation(account);
            return account;
        });
    }

    public Account createAccount(String userId, Double moneyAmount) {
        return executeWithErrorHandling(CREATE_ACCOUNT_CONTEXT, () -> {
            double balance = (moneyAmount != null) ? moneyAmount : getDefaultAmount();
            String accountId = idGeneratorService.generateAccountId();
            return new Account(accountId, userId, balance);  // ← ИСПРАВЛЕН ПОРЯДОК ПАРАМЕТРОВ
        });
    }

    // Метод для обратной совместимости (из старой версии)
    public Account createAccount(String id, String userId, double moneyAmount) {
        Account account = new Account(id, userId, moneyAmount);
        userService.findUserById(userId).ifPresent(user -> {
            user.getAccountList().add(account);
            accountLogger.logAccountCreation(account);
        });
        return account;
    }

    public void showUserAccounts(String userId) {
        executeVoidWithErrorHandling(SHOW_ACCOUNTS_CONTEXT, () -> {
            User user = findUserOrThrow(userId, SHOW_ACCOUNTS_CONTEXT);
            accountLogger.logUserAccounts(user);
        });
    }

    public void deposit(String accountId, double amount) {
        executeVoidWithErrorHandling(DEPOSIT_CONTEXT, () -> {
            validateCommonParameters(accountId, amount, DEPOSIT_CONTEXT);
            Account account = findAccountOrThrow(accountId, DEPOSIT_CONTEXT);
            transactionProcessor.processDeposit(account, amount);
            accountLogger.logDeposit(accountId, amount);
        });
    }

    public void withdraw(String accountId, double amount) {
        executeVoidWithErrorHandling(WITHDRAW_CONTEXT, () -> {
            validateCommonParameters(accountId, amount, WITHDRAW_CONTEXT);
            Account account = findAccountOrThrow(accountId, WITHDRAW_CONTEXT);
            validateCondition(account.getMoneyAmount() >= amount,
                    String.format("Insufficient funds! Available: %.2f, Required: %.2f",
                            account.getMoneyAmount(), amount), WITHDRAW_CONTEXT);
            transactionProcessor.processWithdrawal(account, amount);
            accountLogger.logWithdrawal(accountId, amount);
        });
    }

    public void transfer(String sourceId, String targetId, double amount) {
        executeVoidWithErrorHandling(TRANSFER_CONTEXT, () -> {
            validateTransferParameters(sourceId, targetId, amount);
            Account sourceAccount = findAccountOrThrow(sourceId, TRANSFER_CONTEXT);
            Account targetAccount = findAccountOrThrow(targetId, TRANSFER_CONTEXT);
            boolean isSameUser = sourceAccount.getUserId().equals(targetAccount.getUserId());
            double commission = isSameUser ? 0.0 : getTransferCommission();  // ← ИСПРАВЛЕНО
            double totalRequired = amount + commission;
            validateSufficientFunds(sourceAccount, totalRequired, commission);
            transactionProcessor.processTransfer(sourceAccount, targetAccount, amount, commission);
            accountLogger.logTransfer(sourceId, targetId, amount, commission);
        });
    }

    private void validateTransferParameters(String sourceId, String targetId, double amount) {
        validateNotEmpty(sourceId, "Source Account ID", TRANSFER_CONTEXT);
        validateNotEmpty(targetId, "Target Account ID", TRANSFER_CONTEXT);
        validatePositiveAmount(amount, TRANSFER_CONTEXT);
    }

    public void closeAccount(String accountId, String targetAccountId) {
        executeVoidWithErrorHandling(CLOSE_ACCOUNT_CONTEXT, () -> {
            validateNotEmpty(accountId, "Account ID", CLOSE_ACCOUNT_CONTEXT);
            Account accountToClose = findAccountOrThrow(accountId, CLOSE_ACCOUNT_CONTEXT);
            User user = findUserOrThrow(accountToClose.getUserId(), CLOSE_ACCOUNT_CONTEXT);
            List<Account> userAccounts = user.getAccountList();

            validateCondition(userAccounts.size() > 1,
                    "Cannot close the only account. User must have at least one account.",
                    CLOSE_ACCOUNT_CONTEXT);

            validateCondition(accountToClose.getMoneyAmount() >= 0,
                    String.format("Cannot close account with negative balance: %.2f",
                            accountToClose.getMoneyAmount()), CLOSE_ACCOUNT_CONTEXT);

            Account targetAccount = findTargetAccount(userAccounts, accountId, targetAccountId, CLOSE_ACCOUNT_CONTEXT);
            transferFunds(accountToClose, targetAccount);

            boolean removed = userAccounts.removeIf(acc -> acc.getId().equals(accountId));
            validateCondition(removed, "Failed to remove account " + accountId, CLOSE_ACCOUNT_CONTEXT);

            accountLogger.logAccountClosure(accountId, targetAccount.getId());
        });
    }

    private Account findTargetAccount(List<Account> accounts, String accountIdToClose,
                                      String preferredTargetId, String operationType) {
        if (preferredTargetId != null && !preferredTargetId.trim().isEmpty()) {
            if (preferredTargetId.equals(accountIdToClose)) {
                throw new BankingException(operationType, ErrorType.VALIDATION_ERROR,
                        "Cannot transfer funds to the same account being closed");
            }
            return accounts.stream()
                    .filter(acc -> acc.getId().equals(preferredTargetId))
                    .findFirst()
                    .orElseThrow(() -> new BankingException(operationType, ErrorType.ACCOUNT_NOT_FOUND,
                            String.format("Target account %s not found", preferredTargetId)));
        }
        return accounts.stream()
                .filter(acc -> !acc.getId().equals(accountIdToClose))
                .findFirst()
                .orElseThrow(() -> new BankingException(operationType, ErrorType.ACCOUNT_NOT_FOUND,
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

    // Метод для обратной совместимости (из старой версии)
    public boolean closeAccount(String accountId) {
        try {
            closeAccount(accountId, null);
            return true;
        } catch (Exception e) {
            errorHandler.handleError(CLOSE_ACCOUNT_CONTEXT, "Failed to close account: " + e.getMessage());
            return false;
        }
    }
}