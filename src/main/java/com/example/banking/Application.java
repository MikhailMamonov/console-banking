
package com.example.banking;

import com.example.banking.exception.BankingException;
import com.example.banking.exception.ErrorHandler;
import com.example.banking.exception.ErrorType;
import com.example.banking.model.entity.Account;
import com.example.banking.model.entity.User;
import com.example.banking.service.AccountService;
import com.example.banking.service.OperationsConsoleListener;
import com.example.banking.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
        System.out.println("\n=== DEPOSIT ===");
        String accountId = consoleListener.readLine("Enter account ID: ");
        double amount = consoleListener.readDouble("Enter amount to deposit: ");

        try {
            accountService.deposit(accountId, amount);
        } catch (BankingException e) {
            handleBankingException(e);
        } catch (Exception e) {
            errorHandler.handleError("ACCOUNT_DEPOSIT", "Unexpected error: " + e.getMessage());
        }
    }

    private static void transferOperation(){
        System.out.println("\n=== TRANSFER ===");
        String sourceId = consoleListener.readLine("Enter source account ID: ");
        String targetId = consoleListener.readLine("Enter target account ID: ");
        double amount = consoleListener.readDouble("Enter amount to transfer: ");

        try {
            accountService.transfer(sourceId, targetId, amount);
        } catch (BankingException e) {
            handleBankingException(e);
        } catch (Exception e) {
            errorHandler.handleError("ACCOUNT_TRANSFER", "Unexpected error: " + e.getMessage());
        }
    }

    private static void withdrawOperation(){
        System.out.println("\n=== WITHDRAW ===");
        String accountId = consoleListener.readLine("Enter account ID to withdraw from: ");
        double amount = consoleListener.readDouble("Enter amount to withdraw: ");

        try {
            accountService.withdraw(accountId, amount);
        } catch (BankingException e) {
            handleBankingException(e);
        } catch (Exception e) {
            errorHandler.handleError("ACCOUNT_WITHDRAW", "Unexpected error: " + e.getMessage());
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
            String userIdStr = String.valueOf(userService.getAllUsers().size() + 1);

            List<Account> accounts = new ArrayList<>();
            Account account = accountService.createAccount(userIdStr, null);
            accounts.add(account);

            User user = userService.createUser(login, accounts);
            System.out.printf("✅ User created: %s%n", user);
            System.out.printf("   First account ID: %s with balance: %.2f%n",
                    account.getId(), account.getMoneyAmount());

        } catch (BankingException e) {
            handleBankingException(e);
        } catch (Exception e) {
            errorHandler.handleError("USER_CREATE", "Unexpected error: " + e.getMessage());
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

    private static void handleBankingException(BankingException e) {
        System.err.printf("❌ Error in %s: %s%n", e.getOperationType(), e.getMessage());

        // Специфическая обработка для разных типов ошибок
        switch (e.getErrorType()) {
            case INSUFFICIENT_FUNDS:
                System.err.println("   💡 Please check your balance and try again.");
                break;
            case ACCOUNT_NOT_FOUND:
                System.err.println("   💡 Account does not exist. Please verify the account ID.");
                break;
            case USER_NOT_FOUND:
                System.err.println("   💡 User does not exist. Please verify the user ID.");
                break;
            case VALIDATION_ERROR:
                System.err.println("   💡 Please check your input and try again.");
                break;
            case CLOSING_LAST_ACCOUNT:
                System.err.println("   💡 You cannot close your only account. Create another account first.");
                break;
            case NEGATIVE_BALANCE:
                System.err.println("   💡 Cannot close account with negative balance. Deposit funds first.");
                break;
            case SAME_ACCOUNT_TRANSFER:
                System.err.println("   💡 Cannot transfer funds to the same account.");
                break;
            default:
                System.err.println("   💡 Please contact support if the problem persists.");
        }

        // Детальная информация для отладки (если нужно)
        if (e.getDetails() != null && e.getDetails().length > 0) {
            System.err.print("   Details: ");
            for (int i = 0; i < e.getDetails().length; i += 2) {
                if (i + 1 < e.getDetails().length) {
                    System.err.printf("%s=%s ", e.getDetails()[i], e.getDetails()[i + 1]);
                }
            }
            System.err.println();
        }
    }

    private static Optional<Account> findAccountById(String accountId) {
        return userService.getAllUsers().stream()
                .flatMap(user -> user.getAccountList().stream())
                .filter(account -> account.getId().equals(accountId))
                .findFirst();
    }
}