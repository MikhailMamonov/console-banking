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

<<<<<<< Updated upstream
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
=======
    private static final double DEFAULT_BALANCE_ON_ERROR = 0.0;
    private static final String CONFIGURATION_ERROR_CONTEXT = "CONFIGURATION";
    private static final String CREATE_ACCOUNT_CONTEXT = "CREATE_ACCOUNT";
    private static final String DEPOSIT_CONTEXT = "DEPOSIT";
    private static final String WITHDRAW_CONTEXT = "WITHDRAW";
    private static final String TRANSFER_CONTEXT = "TRANSFER";
    private static final String CLOSE_ACCOUNT_CONTEXT = "CLOSE_ACCOUNT";
    private static final String SHOW_ACCOUNTS_CONTEXT = "SHOW_ACCOUNTS";

    @Value("${account.default-amount:0.0}")
    private String defaultAmount;

    @Value("${account.transfer-commission:0.0}")
    private String transferCommission;

    private final IdGeneratorService idGeneratorService;

    private final AccountTransactionProcessor transactionProcessor;
    private final AccountLogger accountLogger;


    public AccountService(AccountTransactionProcessor transactionProcessor,
                          AccountLogger accountLogger,
                          IdGeneratorService idGeneratorService) {
        this.transactionProcessor = transactionProcessor;
        this.accountLogger = accountLogger;
        this.idGeneratorService = idGeneratorService;
    }

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

    public Account createAccountForUser(String userId) {
        return executeWithErrorHandling(CREATE_ACCOUNT_CONTEXT, () -> {
            validateNotEmpty(userId, "User ID", CREATE_ACCOUNT_CONTEXT);
            User user = findUserOrThrow(userId, CREATE_ACCOUNT_CONTEXT);
            Account account = createAccount(userId, null);
            user.getAccountList().add(account); // КЛЮЧЕВОЕ: связываем аккаунт с пользователем
            return account;
        });
    }

    public Account createAccount(String userId, Double moneyAmount) {
        return executeWithErrorHandling(CREATE_ACCOUNT_CONTEXT, () -> {
            double balance = (moneyAmount != null) ? moneyAmount : getDefaultAmount();
            String accountId = idGeneratorService.generateAccountId();

            return new Account(userId, balance, accountId);
        });
    }

    public void showUserAccounts(String userId) {
        executeVoidWithErrorHandling(SHOW_ACCOUNTS_CONTEXT, () -> {
            User user = findUserOrThrow(userId, SHOW_ACCOUNTS_CONTEXT);
            accountLogger.logUserAccounts(user);
>>>>>>> Stashed changes
        });

    }

    public void deposit(String accountId, double amount) {
<<<<<<< Updated upstream
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
=======
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
            double commission = isSameUser ? DEFAULT_BALANCE_ON_ERROR: getTransferCommission();
            double totalRequired = amount + commission;

            validateSufficientFunds(sourceAccount, totalRequired, commission);

            // Передаем комиссию в процессор
            transactionProcessor.processTransfer(sourceAccount, targetAccount, amount, commission);
            accountLogger.logTransfer(sourceId, targetId, amount, commission);
        });
    }

    private void validateTransferParameters(String sourceId, String targetId, double amount) {
        validateNotEmpty(sourceId, "Source Account ID", TRANSFER_CONTEXT);
        validateNotEmpty(targetId, "Target Account ID", TRANSFER_CONTEXT);
        validatePositiveAmount(amount, TRANSFER_CONTEXT);
    }

    private void validateSufficientFunds(Account account, double requiredAmount, double commission) {
        if (account.getMoneyAmount() < requiredAmount) {
            String message = buildInsufficientFundsMessage(account.getMoneyAmount(), requiredAmount, commission);
            throw new BankingException(TRANSFER_CONTEXT, ErrorType.INSUFFICIENT_FUNDS, message);
        }
    }

    public void closeAccount(String accountId, String targetAccountId) {
        executeVoidWithErrorHandling(CLOSE_ACCOUNT_CONTEXT, () -> {
            validateNotEmpty(accountId, "Account ID", CLOSE_ACCOUNT_CONTEXT);

            Account accountToClose = findAccountOrThrow(accountId, CLOSE_ACCOUNT_CONTEXT);
            User user = findUserOrThrow(accountToClose.getUserId(), CLOSE_ACCOUNT_CONTEXT);
            List<Account> userAccounts = user.getAccountList();

            // Валидации
            validateCondition(userAccounts.size() > 1,
                    "Cannot close the only account. User must have at least one account.",
                    CLOSE_ACCOUNT_CONTEXT);

            validateCondition(accountToClose.getMoneyAmount() >= 0,
                    String.format("Cannot close account with negative balance: %.2f",
                            accountToClose.getMoneyAmount()), CLOSE_ACCOUNT_CONTEXT);

            // Поиск целевого счета
            Account targetAccount = findTargetAccount(userAccounts, accountId, targetAccountId, CLOSE_ACCOUNT_CONTEXT);

            // Перевод средств
            transferFunds(accountToClose, targetAccount);

            // Удаление счета
            boolean removed = userAccounts.removeIf(acc -> acc.getId().equals(accountId));
            validateCondition(removed, "Failed to remove account " + accountId, CLOSE_ACCOUNT_CONTEXT);

            accountLogger.logAccountClosure(accountId, targetAccount.getId());
        });
    }

    private Account findTargetAccount(List<Account> accounts, String accountIdToClose,
                                      String preferredTargetId, String operationType) {
        if (preferredTargetId != null && !preferredTargetId.trim().isEmpty()) {
            // Проверка, что не пытаемся перевести на тот же счет
            if (preferredTargetId.equals(accountIdToClose)) {
                throw new BankingException(operationType, ErrorType.VALIDATION_ERROR,
                        "Cannot transfer funds to the same account being closed");
            }
            return accounts.stream()
                    .filter(acc -> acc.getId().equals(preferredTargetId))
                    .findFirst()
                    .orElseThrow(() -> new BankingException(operationType, ErrorType.NOT_FOUND,
                            String.format("Target account %s not found", preferredTargetId)));
>>>>>>> Stashed changes
        }

        Account account = optionalAccount.get();

<<<<<<< Updated upstream
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
=======
    private void transferFunds(Account source, Account target) {
        double amount = source.getMoneyAmount();
        if (amount > DEFAULT_BALANCE_ON_ERROR) {
            target.setMoneyAmount(target.getMoneyAmount() + amount);
            source.setMoneyAmount(0.0);
            // Комиссия при закрытии счета не взимается
            accountLogger.logFundsTransfer(amount, source.getId(), target.getId());
>>>>>>> Stashed changes
        }

<<<<<<< Updated upstream
        // 8. Удаление счета
        boolean removed = accountList.removeIf(item -> item.getId().equals(accountId));

        if (!removed) {
            System.out.printf("Failed to remove account %s from user %s", accountId, user.getId());
            return false;
        }

        return true;
=======
    private String buildInsufficientFundsMessage(double available, double required, double commission) {
        if (commission > 0) {
            return String.format("Insufficient funds! Available: %.2f, Required: %.2f (amount: %.2f + commission: %.2f)",
                    available, required, required - commission, commission);
        }
        return String.format("Insufficient funds! Available: %.2f, Required: %.2f", available, required);
>>>>>>> Stashed changes
    }
}
