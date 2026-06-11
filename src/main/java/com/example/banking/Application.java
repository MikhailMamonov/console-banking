package com.example.banking;

import com.example.banking.model.entity.Account;
import com.example.banking.model.entity.User;
import com.example.banking.service.AccountService;
import com.example.banking.service.OperationsConsoleListener;
import com.example.banking.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;


public class Application {

    private static UserService userService;
    private static AccountService accountService;
    private static OperationsConsoleListener consoleListener;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(SpringConfig.class);
        userService = context.getBean(UserService.class);
        accountService = context.getBean(AccountService.class);
        consoleListener = context.getBean(OperationsConsoleListener.class);

        System.out.println("Добро пожаловать в банковское приложение!");

        while(true){
            printMenu();
            String choice = consoleListener.readLine("\nPlease enter one of operation type: ");
            switch (choice){
                case "USER_CREATE":
                    createNewUser();
                    break;
                case "SHOW_ALL_USERS":
                    showAllUsers();
                    break;
                case "ACCOUNT_CREATE":
                    createNewAccount();
                    break;
                case "ACCOUNT_CLOSE":
                    closeAccount();
                    break;
                case "ACCOUNT_DEPOSIT":
                    depositOperation();
                    break;

                case "0":
                    System.out.println("До свидания!");
                    context.close();
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }

        }
    }

    private static void closeAccount() {
        String accountId = consoleListener.readLine("Введите ID аккаунта: ");
        accountService.closeAccount(accountId);
    }

    private static void depositOperation() {
        String accountId = consoleListener.readLine("Введите ID аккаунта: ");
        double amount = consoleListener.readDouble("Введите сумму пополнения: ");
        accountService.deposit(accountId, amount);
    }

    private static void showAllUsers() {
        userService.showAllUsers();
    }

    private static void showUserAccounts() {
        String userId = consoleListener.readLine("Введите ID пользователя: ");
        accountService.showUserAccounts(userId);
    }

    private static void createNewAccount() {
        System.out.println("\n=== СОЗДАНИЕ НОВОГО АККАУНТА ===");

        String userId = consoleListener.readLine("Введите ID пользователя: ");

        if (!userService.userExists(userId)) {
            System.out.println("Пользователь с таким ID не найден!");
            return;
        }

        Account account = createAccountInput(userId);
        accountService.createAccount(account.getId(), userId, account.getMoneyAmount());
    }

    private static void createNewUser() {
        System.out.println("=== Создание пользователя ===");
        String id = consoleListener.readLine("Введите ID пользователя: ");
        if (userService.userExists(id)){
            System.out.println("Пользователь с таким ИД уже создан.");
            return;
        }
        String login = consoleListener.readLine("Введите login пользователя: ");

        String createAccounts = consoleListener.readLine("Создать аккаунты для пользователя? (да/нет): ");
        List<Account> accounts = new ArrayList<>();
        if (createAccounts.equalsIgnoreCase("да")) {
            int count = consoleListener.readInt("Сколько аккаунтов создать? ");
            for (int i = 0; i < count; i++) {
                System.out.println("\nАккаунт " + (i + 1) + ":");
                Account account = createAccountInput(id);
                accounts.add(account);
            }
        }

        userService.createUser(id, login, accounts);
    }

    private static Account createAccountInput(String userId) {
        String accountId = consoleListener.readLine("Введите ID аккаунта: ");
        double balance = consoleListener.readDouble("Введите начальный баланс: ");
        return new Account(accountId, userId, balance);
    }

    private static void printMenu() {
        System.out.println("\n=== БАНКОВСКОЕ ПРИЛОЖЕНИЕ ===");
        System.out.println("USER_CREATE. Создать нового пользователя");
        System.out.println("SHOW_ALL_USERS. Показать всех пользователей");
        System.out.println("ACCOUNT_CREATE. Создать аккаунт.");
        System.out.println("ACCOUNT_CLOSE. Закрыть аккаунт.");
        System.out.println("ACCOUNT_DEPOSIT. Пополнить счет аккаунта.");
    }


}