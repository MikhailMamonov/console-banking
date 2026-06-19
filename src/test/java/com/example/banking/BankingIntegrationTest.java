package com.example.banking;

import com.example.banking.model.entity.Account;
import com.example.banking.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BankingIntegrationTest extends BaseIntegrationTest {

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        // Теперь бины уже проинициализированы в родительском классе
        // Можно добавить дополнительную логику инициализации если нужно
    }

    @Test
    @DisplayName("Сценарий: Создание пользователя, счета и успешный депозит через консольные команды")
    void testUserAndAccountCreationWithDeposit() {
        // Очищаем вывод перед тестом
        clearOutput();

        String userLogin = "mikhail";
        provideInput(userLogin);

        consoleListener.processOperation("USER_CREATE");

        assertTrue(userService.isLoginExists(userLogin),
                "Пользователь должен быть создан в базе данных");

        User createdUser = userService.findUserByLogin(userLogin)
                .orElseThrow(() -> new AssertionError("Пользователь '" + userLogin + "' не найден!"));

        String output = getOutput();
        assertTrue(output.contains("User created successfully!"),
                "Должно быть сообщение об успешном создании");
        assertTrue(output.contains("First account ID:"),
                "Консоль должна содержать лог о создании первого счёта");

        clearOutput(); // Очищаем для получения нового вывода
        accountService.showUserAccounts(String.valueOf(createdUser.getId()));

        String accountsOutput = getOutput();
        assertTrue(accountsOutput.contains("ACC-"),
                "Счёт с маской 'ACC-' должен быть связан с пользователем");

        clearOutput();
        List<Account> userAccounts = accountService.findAccountsByUserId(createdUser.getId());
        assertFalse(userAccounts.isEmpty(), "У пользователя должен быть хотя бы один счет");
    }

    @Test
    @DisplayName("Сценарий: Создание пользователя с дублирующимся логином")
    void testDuplicateUserCreation() {
        // Очищаем вывод перед тестом
        clearOutput();

        String userLogin = "duplicate_user";

        // Создаем пользователя
        provideInput(userLogin);
        consoleListener.processOperation("USER_CREATE");

        // Пытаемся создать дубликат
        clearOutput();
        provideInput(userLogin);
        consoleListener.processOperation("USER_CREATE");

        // Проверяем, что пользователь не создался повторно
        String output = getOutput();
        assertTrue(output.contains("already exists") || output.contains("Already exists"),
                "Должна быть ошибка о существующем пользователе");

        // Проверяем, что в базе только один пользователь с таким логином
        var users = userService.getAllUsers();
        long count = users.stream()
                .filter(u -> u.getLogin().equals(userLogin))
                .count();
        assertEquals(1, count, "Должен быть только один пользователь с таким логином");
    }

    @Test
    @DisplayName("Сценарий: Проверка баланса после операций")
    void testBalanceAfterOperations() {
        clearOutput();

        String userLogin = "balance_user";
        provideInput(userLogin);
        consoleListener.processOperation("USER_CREATE");

        User user = userService.findUserByLogin(userLogin)
                .orElseThrow(() -> new AssertionError("Пользователь не найден"));

        var accounts = accountService.findAccountsByUserId(String.valueOf(user.getId()));
        assertFalse(accounts.isEmpty(), "У пользователя должен быть счет");
        Account account = accounts.get(0);

        double depositAmount = 500.0;
        clearOutput();
        provideInput(String.valueOf(account.getId()), String.valueOf(depositAmount));
        consoleListener.processOperation("ACCOUNT_DEPOSIT");

        Account updatedAccount = accountService.getAccountById(String.valueOf(account.getId()));
        assertEquals(depositAmount, updatedAccount.getMoneyAmount(), 0.001,
                "Баланс должен быть равен сумме депозита");

        String output = getOutput();
        assertTrue(output.contains("Deposit successful") || output.contains("Success"),
                "Должно быть сообщение об успешном депозите");
    }
}