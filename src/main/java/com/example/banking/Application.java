package com.example.banking;

import com.example.banking.exception.BankingException;
import com.example.banking.exception.ErrorHandler;
import com.example.banking.model.entity.Account;
import com.example.banking.model.entity.User;
import com.example.banking.service.AccountService;
import com.example.banking.service.OperationsConsoleListener;
import com.example.banking.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {

    private static UserService userService;
    private static AccountService accountService;
    private static OperationsConsoleListener consoleListener;
    private static ErrorHandler errorHandler;

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(SpringConfig.class)) {

            userService = context.getBean(UserService.class);
            accountService = context.getBean(AccountService.class);
            consoleListener = context.getBean(OperationsConsoleListener.class);
            errorHandler = context.getBean(ErrorHandler.class);

            System.out.println("Welcome to the Banking Application!");

            while (true) {
                printMenu();
                String choice = consoleListener.readLine("\nPlease enter one of operation type: ");

                switch (choice) {
                    case "USER_CREATE" -> createNewUser();
                    case "SHOW_ALL_USERS" -> showAllUsers();
                    case "SHOW_USER_ACCOUNTS" -> showUserAccounts();
                    case "ACCOUNT_CREATE" -> createNewAccount();
                    case "ACCOUNT_CLOSE" -> closeAccount();
                    case "ACCOUNT_DEPOSIT" -> depositOperation();
                    case "ACCOUNT_TRANSFER" -> transferOperation();
                    case "ACCOUNT_WITHDRAW" -> withdrawOperation();
                    case "0" -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

    // === USER OPERATIONS ===

    private static void createNewUser() {
        String login = consoleListener.readLine("Enter login for new user: ");

        if (userService.isLoginExists(login)) {
            System.out.println("User with this login already exists!");
            return;
        }

        try {
            User user = userService.createUser(login);
            Account account = accountService.createAccountForUser(user.getId());

            System.out.printf("✅ User created: %s%n", user);
            System.out.printf("   First account ID: %s with balance: %.2f%n",
                    account.getId(), account.getMoneyAmount());

        } catch (BankingException e) {
            handleBankingException(e);
        } catch (Exception e) {
            errorHandler.handleError("USER_CREATE", "Unexpected error: " + e.getMessage());
        }
    }

    private static void showAllUsers() {
        userService.showAllUsers();
    }

    private static void showUserAccounts() {
        String userId = consoleListener.readLine("Enter user ID: ");
        accountService.showUserAccounts(userId);
    }

    // === ACCOUNT OPERATIONS ===

    private static void createNewAccount() {
        String userId = consoleListener.readLine("Enter the user ID for which to create an account: ");

        if (!userService.userExists(userId)) {
            System.out.println("User with entered ID not found!");
            return;
        }

        try {
            Account account = accountService.createAccountForUser(userId);
            String login = userService.findUserById(userId)
                    .map(User::getLogin)
                    .orElse("Unknown");

            System.out.printf("✅ New account created. ID: %s for user: %s%n",
                    account.getId(), login);
        } catch (BankingException e) {
            handleBankingException(e);
        } catch (Exception e) {
            errorHandler.handleError("ACCOUNT_CREATE", "Unexpected error: " + e.getMessage());
        }
    }

    private static void closeAccount() {
        String accountId = consoleListener.readLine("Enter account ID to close: ");

        try {
            accountService.closeAccount(accountId, null);
            System.out.printf("✅ Account %s successfully closed%n", accountId);
        } catch (BankingException e) {
            handleBankingException(e);
        } catch (Exception e) {
            errorHandler.handleError("ACCOUNT_CLOSE", "Unexpected error: " + e.getMessage());
        }
    }

    // === FINANCIAL OPERATIONS ===

    private static void depositOperation() {
        System.out.println("\n=== DEPOSIT ===");
        String accountId = consoleListener.readLine("Enter account ID: ");
        double amount = consoleListener.readDouble("Enter amount to deposit: ");

        try {
            accountService.deposit(accountId, amount);
            System.out.printf("✅ Account %s successfully deposited with %.2f%n", accountId, amount);
        } catch (BankingException e) {
            handleBankingException(e);
        } catch (Exception e) {
            errorHandler.handleError("ACCOUNT_DEPOSIT", "Unexpected error: " + e.getMessage());
        }
    }

    private static void withdrawOperation() {
        System.out.println("\n=== WITHDRAW ===");
        String accountId = consoleListener.readLine("Enter account ID: ");
        double amount = consoleListener.readDouble("Enter amount to withdraw: ");

        try {
            accountService.withdraw(accountId, amount);
            System.out.printf("✅ %.2f successfully withdrawn from account %s%n", amount, accountId);
        } catch (BankingException e) {
            handleBankingException(e);
        } catch (Exception e) {
            errorHandler.handleError("ACCOUNT_WITHDRAW", "Unexpected error: " + e.getMessage());
        }
    }

    private static void transferOperation() {
        System.out.println("\n=== TRANSFER ===");
        String sourceId = consoleListener.readLine("Enter source account ID: ");
        String targetId = consoleListener.readLine("Enter target account ID: ");
        double amount = consoleListener.readDouble("Enter amount to transfer: ");

        try {
            accountService.transfer(sourceId, targetId, amount);
            System.out.printf("✅ Transfer of %.2f from account %s to account %s completed successfully%n",
                    amount, sourceId, targetId);
        } catch (BankingException e) {
            handleBankingException(e);
        } catch (Exception e) {
            errorHandler.handleError("ACCOUNT_TRANSFER", "Unexpected error: " + e.getMessage());
        }
    }

    // === UI ===

    private static void printMenu() {
        System.out.println("\n╔═══════════════════════════════════════════╗");
        System.out.println("║         BANKING APPLICATION             ║");
        System.out.println("╠═══════════════════════════════════════════╣");
        System.out.println("║ USER_CREATE       - Create new user      ║");
        System.out.println("║ SHOW_ALL_USERS    - Show all users       ║");
        System.out.println("║ SHOW_USER_ACCOUNTS - Show user accounts  ║");
        System.out.println("║ ACCOUNT_CREATE    - Create new account   ║");
        System.out.println("║ ACCOUNT_CLOSE     - Close account        ║");
        System.out.println("║ ACCOUNT_DEPOSIT   - Deposit to account   ║");
        System.out.println("║ ACCOUNT_WITHDRAW  - Withdraw from account║");
        System.out.println("║ ACCOUNT_TRANSFER  - Transfer funds       ║");
        System.out.println("║ 0                 - Exit                 ║");
        System.out.println("╚═══════════════════════════════════════════╝");
    }

    // === ERROR HANDLING ===

    private static void handleBankingException(BankingException e) {
        System.err.printf("❌ Error in %s: %s%n", e.getOperationType(), e.getMessage());

        if (e.getErrorType() != null) {
            String hint = switch (e.getErrorType()) {
                case INSUFFICIENT_FUNDS -> "💡 Insufficient funds. Please check your balance.";
                case ACCOUNT_NOT_FOUND -> "💡 Account does not exist. Please verify the account ID.";
                case USER_NOT_FOUND -> "💡 User does not exist. Please verify the user ID.";
                case VALIDATION_ERROR -> "💡 Validation error. Please check your input.";
                case CLOSING_LAST_ACCOUNT -> "💡 Cannot close your only account. Create another account first.";
                case NEGATIVE_BALANCE -> "💡 Cannot close account with negative balance. Deposit funds first.";
                case SAME_ACCOUNT_TRANSFER -> "💡 Cannot transfer funds to the same account.";
                default -> "💡 Please contact support if the problem persists.";
            };
            System.err.println("   " + hint);
        }

        // Detailed information for debugging
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
}