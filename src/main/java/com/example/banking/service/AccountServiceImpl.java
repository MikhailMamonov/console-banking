package com.example.banking.service;

import com.example.banking.exception.BankingException;
import com.example.banking.exception.ErrorType;
import com.example.banking.model.entity.Account;
import com.example.banking.model.entity.User;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of AccountService with PostgreSQL integration.
 */
@Service
public class AccountServiceImpl implements AccountService {

    private static final double DEFAULT_BALANCE_ON_ERROR = 0.0;

// Сразу считываем настройки как double, избавляясь от String и parseDouble
@Value("${account.default-amount:0.0}")
private double defaultAmount;

@Value("${account.transfer-commission:0.0}")
private double transferCommission;

private final AtomicLong idCounter = new AtomicLong(100000);

private final UserRepository userRepository;
private final AccountRepository accountRepository;
private final AccountTransactionProcessor transactionProcessor;
private final AccountLogger accountLogger;
private final AccountValidator accountValidator;

public AccountServiceImpl(UserRepository userRepository,
                          AccountRepository accountRepository,
                          AccountTransactionProcessor transactionProcessor,
                          AccountLogger accountLogger,
                          AccountValidator accountValidator) {
    this.userRepository = userRepository;
    this.accountRepository = accountRepository;
    this.transactionProcessor = transactionProcessor;
    this.accountLogger = accountLogger;
    this.accountValidator = accountValidator;
}

// === Helper Methods ===

private User findUserOrThrow(String userId) {
    return userRepository.findById(userId)
            .orElseThrow(() -> new BankingException("ACCOUNT_OPERATION", ErrorType.USER_NOT_FOUND,
                    String.format("User with ID %s not found", userId)));
}

private Account findAccountOrThrow(String accountId) {
    return findAccountById(accountId)
            .orElseThrow(() -> new BankingException("ACCOUNT_OPERATION", ErrorType.ACCOUNT_NOT_FOUND,
                    String.format("Account with ID %s not found", accountId)));
}

private Optional<Account> findAccountById(String accountId) {
    // Прямой быстрый поиск в базе по первичному ключу вместо стримов
    return accountRepository.findById(accountId);
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

        accountRepository.save(source);
        accountRepository.save(target);
        accountLogger.logFundsTransfer(amount, source.getId(), target.getId());
    }
}

// === Public Methods ===

@Override
@Transactional
public Account createAccountForUser(String userId) {
    accountValidator.validateUser(userId, "CREATE_ACCOUNT_FOR_USER");

    User user = findUserOrThrow(userId);
    Account account = createAccount(userId, null);

    // Из-за каскадности связи сохранение аккаунта обновит и список у пользователя
    Account savedAccount = accountRepository.save(account);
    accountLogger.logAccountCreation(savedAccount);
    return savedAccount;
}

@Override
public Account createAccount(String userId, Double moneyAmount) {
    accountValidator.validateUser(userId, "CREATE_ACCOUNT");

    double balance = (moneyAmount != null) ? moneyAmount : defaultAmount;
    String accountId = "ACC-" + idCounter.incrementAndGet();
    return new Account(accountId, userId, balance);
}

@Override
@Transactional(readOnly = true)
public void showUserAccounts(String userId) {
    accountValidator.validateUser(userId, "SHOW_USER_ACCOUNTS");

    User user = findUserOrThrow(userId);
    accountLogger.logUserAccounts(user);
}

@Override
@Transactional
public void deposit(String accountId, double amount) {
    accountValidator.validateTransaction(accountId, amount, "DEPOSIT");

    Account account = findAccountOrThrow(accountId);
    transactionProcessor.processDeposit(account, amount);

    accountRepository.save(account); // Синхронизируем баланс с БД
    accountLogger.logDeposit(accountId, amount);
}

@Override
@Transactional
public void withdraw(String accountId, double amount) {
    accountValidator.validateTransaction(accountId, amount, "WITHDRAW");

    Account account = findAccountOrThrow(accountId);
    accountValidator.validateCondition(account.getMoneyAmount() >= amount,
            String.format("Insufficient funds! Available: %.2f, Required: %.2f",
                    account.getMoneyAmount(), amount), "WITHDRAW");

    transactionProcessor.processWithdrawal(account, amount);

    accountRepository.save(account); // Синхронизируем баланс с БД
    accountLogger.logWithdrawal(accountId, amount);
}

@Override
@Transactional
public void transfer(String sourceId, String targetId, double amount) {
    accountValidator.validateTransfer(sourceId, targetId, amount);

    Account sourceAccount = findAccountOrThrow(sourceId);
    Account targetAccount = findAccountOrThrow(targetId);
    boolean isSameUser = sourceAccount.getUserId().equals(targetAccount.getUserId());
    double commission = isSameUser ? 0.0 : transferCommission;
    double totalRequired = amount + commission;

    accountValidator.validateSufficientFunds(sourceAccount, totalRequired, commission);
    transactionProcessor.processTransfer(sourceAccount, targetAccount, amount, commission);

    // Сохраняем оба аккаунта
    accountRepository.save(sourceAccount);
    accountRepository.save(targetAccount);

    accountLogger.logTransfer(sourceId, targetId, amount, commission);
}

@Override
@Transactional
public void closeAccount(String accountId, String targetAccountId) {
    accountValidator.validateUser(accountId, "CLOSE_ACCOUNT");

    Account accountToClose = findAccountOrThrow(accountId);
    User user = findUserOrThrow(accountToClose.getUserId());
    List<Account> userAccounts = user.getAccountList();

    accountValidator.validateCondition(userAccounts.size() > 1,
            "Cannot close the only account. User must have at least one account.",
            "CLOSE_ACCOUNT");

    accountValidator.validateCondition(accountToClose.getMoneyAmount() >= 0,
            String.format("Cannot close account with negative balance: %.2f",
                    accountToClose.getMoneyAmount()), "CLOSE_ACCOUNT");

    Account targetAccount = findTargetAccount(userAccounts, accountId, targetAccountId);
    transferFunds(accountToClose, targetAccount);

    // Физически удаляем аккаунт из таблицы accounts в PostgreSQL
    accountRepository.delete(accountToClose);

    accountLogger.logAccountClosure(accountId, targetAccount.getId());
}

    @Override
    @Transactional(readOnly = true) // Оптимизирует чтение из PostgreSQL (без лишних блокировок строк)
    public Account getAccountById(String accountId) {
        // Используем готовый валидированный поиск
        return findAccountOrThrow(accountId);
    }
}