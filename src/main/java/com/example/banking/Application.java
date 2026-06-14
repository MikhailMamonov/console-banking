
package com.example.banking;

import com.example.banking.exception.ErrorHandler;
import com.example.banking.model.entity.Account;
import com.example.banking.model.entity.User;
import com.example.banking.service.AccountService;
import com.example.banking.service.OperationsConsoleListener;
import com.example.banking.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;


public class Application {

    private static UserService userService;
    private static AccountService accountService;
    private static OperationsConsoleListener consoleListener;
    private static ErrorHandler errorHandler;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(SpringConfig.class);
        userService = context.getBean(UserService.class);
        accountService = context.getBean(AccountService.class);
        consoleListener = context.getBean(OperationsConsoleListener.class);
        errorHandler = context.getBean(ErrorHandler.class);

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
                case "SHOW_USER_ACCOUNTS":
                    showUserAccounts();
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
                case "ACCOUNT_TRANSFER":
                    transferOperation();
                    break;
                case "ACCOUNT_WITHDRAW":
                    withdrawOperation();
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
        String accountId = consoleListener.readLine("Enter account ID: ");
        try {
            accountService.closeAccount(accountId, null);
        } catch (Exception e) {
            errorHandler.handleError("ACCOUNT_CLOSE", e.getMessage());
        }
    }

    private static void depositOperation() {
        String accountId = consoleListener.readLine("Enter account ID: ");
        double amount = consoleListener.readDouble("Enter amount to deposit:");
        try {
            accountService.deposit(accountId, amount);
        } catch (Exception e) {
            errorHandler.handleError("ACCOUNT_DEPOSIT", e.getMessage());
        }
    }

    private static void transferOperation(){
        String sourceId = consoleListener.readLine("Enter source account ID:");
        String targetId = consoleListener.readLine("Enter target account ID:");
        double amount = consoleListener.readDouble("Enter amount to transfer");
        try {
            accountService.transfer(sourceId, targetId, amount);
        } catch (Exception e) {
            errorHandler.handleError("ACCOUNT_TRANSFER", e.getMessage());
        }
    }

    private static void withdrawOperation(){
        String accountId = consoleListener.readLine("Enter account ID to withdraw from:");
        double amount = consoleListener.readDouble("Enter amount to withdraw:");
        try {
            accountService.withdraw(accountId, amount);
        } catch (Exception e) {
            errorHandler.handleError("ACCOUNT_WITHDRAW", e.getMessage());
        }
    }

    private static void showAllUsers() {
        userService.showAllUsers();
    }

    private static void showUserAccounts() {
        String userId = consoleListener.readLine("Enter user ID: ");
        accountService.showUserAccounts(userId);
    }

    private static void createNewAccount() {
        String userId = consoleListener.readLine("Enter the user id for which to create an account: ");

        if (!userService.userExists(userId)) {
            System.out.println("User with entered id not found!");
            return;
        }

        String login = userService.findUserById(userId)
                .map(User::getLogin)
                .orElse("Unknown");

        try {
            Account account = accountService.createAccountForUser(userId);
            System.out.println(String.format("New account created with ID: %s for user: %s",
                    account.getId(), login));
        } catch (Exception e) {
            errorHandler.handleError("ACCOUNT_CREATE", e.getMessage());
        }
    }

    private static void createNewUser() {
        String login = consoleListener.readLine("Enter login for new user:");
        if (userService.findUserByLogin(login).isPresent()) {
            System.out.println("User with login already exists!");
            return;
        }

        try {
            int userId = userService.getAllUsers().size() + 1;
            String userIdStr = String.valueOf(userId);

            List<Account> accounts = new ArrayList<>();
            Account account = accountService.createAccount(userIdStr, null);
            accounts.add(account);

            User user = userService.createUser(login, accounts);

            System.out.println("User created: " + user);
        } catch (Exception e) {
            errorHandler.handleError("USER_CREATE", e.getMessage());
        }
    }

    private static void printMenu() {
        System.out.println("\n=== БАНКОВСКОЕ ПРИЛОЖЕНИЕ ===");
        System.out.println("USER_CREATE. Создать нового пользователя");
        System.out.println("SHOW_ALL_USERS. Показать всех пользователей");
        System.out.println("SHOW_USER_ACCOUNTS. Показать счета пользователя");
        System.out.println("ACCOUNT_CREATE. Создать аккаунт.");
        System.out.println("ACCOUNT_CLOSE. Закрыть аккаунт.");
        System.out.println("ACCOUNT_DEPOSIT. Пополнить счет аккаунта.");
        System.out.println("ACCOUNT_TRANSFER. Перевести на другой счёт." );
        System.out.println("ACCOUNT_WITHDRAW. Вывести средства со счета");
        System.out.println("0. Выход");
    }
}