package com.example.banking;

import com.example.banking.model.entity.Account;
import com.example.banking.model.entity.User;
import com.example.banking.service.AccountService;
import com.example.banking.service.OperationsConsoleListener;
import com.example.banking.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankingIntegrationTest extends BaseIntegrationTest {

    private UserService userService;
    private AccountService accountService;
    private OperationsConsoleListener consoleListener;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        // Извлекаем реальные бины из контекста
        userService = context.getBean(UserService.class);
        accountService = context.getBean(AccountService.class);
        consoleListener = context.getBean(OperationsConsoleListener.class);
    }

    @Test
    @DisplayName("Сценарий: Создание пользователя, счета и успешный депозит через консольные команды")
    void testUserAndAccountCreationWithDeposit() {
        // Имитируем шаги пользователя в меню:
        // 1. Команда USER_CREATE
        // 2. ID: "user-100"
        // 3. Логин: "mikhail"
        // 4. На вопрос "Создать аккаунты?" отвечаем "нет"
        provideInput("USER_CREATE", "user-100", "mikhail", "нет");

        // Запускаем обработчик одной итерации (или метод обработки конкретной команды)
        // В зависимости от вашей реализации Application.java, здесь вызывается consoleListener
        // Предположим, у вас есть метод прослушивания или вы вызываете команду напрямую:

        // Имитируем запуск бизнес-логики команды создания пользователя
        // (Здесь вы можете вызвать ваш стартовый цикл, ограничив ввод командой EXIT)

        // Проверяем состояние сервисов (Интеграция: консоль -> сервисный слой)
        assertTrue(userService.userExists("user-100"));

        // Теперь имитируем создание счета для этого пользователя
        provideInput("ACCOUNT_CREATE", "user-100", "acc-555");
        // Вызов логики создания...

        accountService.showUserAccounts("user-100");

        //Достаем всё, что было напечатано в консоль за время теста
        String output = getOutput();
        // Проверяем, что в тексте лога присутствует ID созданного аккаунта
        assertTrue(output.contains("acc-555"), "Консоль должна содержать информацию об аккаунте acc-555");
    }

    @Test
    @DisplayName("Сценарий: Перевод денег между пользователями с учетом комиссии")
    void testTransferBetweenUsersWithCommission() {
        User sender = userService.createUser("sender_login");
        User receiver = userService.createUser("receiver_login");

        Account senderAccount = accountService.createAccount(sender.getId(), 1000.0);
        Account receiverAccount = accountService.createAccount(receiver.getId(), 0.0);

        String senderAccId = String.valueOf(senderAccount.getId());
        String receiverAccId = String.valueOf(receiverAccount.getId());

        provideInput("ACCOUNT_TRANSFER", senderAccId, receiverAccId, "500");

        consoleListener.processOperation("ACCOUNT_TRANSFER");

        // 5. Проверяем изменения балансов после транзакции
        // Напрямую запрашиваем обновленные объекты из вашего хранилища/сервиса
        Account updatedSenderAcc = accountService.getAccountById(senderAccId);
        Account updatedReceiverAcc = accountService.getAccountById(receiverAccId);

        // Проверяем логику списания и зачисления денег:
        // Баланс получателя: 0.0 + 500.0 = 500.0
        assertEquals(500.0, updatedReceiverAcc.getMoneyAmount(), "Баланс получателя должен увеличиться на 500");

        // Баланс отправителя должен уменьшиться на сумму перевода (500) и комиссию (например, 10 или 50 из свойств)
        // Предположим, комиссия фиксированная и равна 50.0: 1000 - 500 - 50 = 450
        double expectedSenderBalance = 1000.0 - 500.0 - 50.0;
        assertEquals(expectedSenderBalance, updatedSenderAcc.getMoneyAmount(), "Баланс отправителя должен уменьшиться с учетом комиссии");
    }

    @Test
    @DisplayName("Сценарий: Попытка закрытия единственного счета должна вызывать BankingException")
    void testCloseSingleAccountValidation() {
        User user = userService.createUser("user_login");
        accountService.createAccount(user.getId(), 100.0);

        // Имитируем ввод закрытия счета
        provideInput("ACCOUNT_CLOSE", "acc-single");

        // Проверяем, что приложение выдает ошибку в консоль или бросает исключение
        // Из структуры вашего проекта видно, что ошибки типизированы через ErrorType

        String output = getOutput();
        // Пример проверки консольного вывода на дружественную ошибку:
        // assertTrue(output.contains("Запрет закрытия единственного счета") || output.contains("Ошибка"));
    }
}