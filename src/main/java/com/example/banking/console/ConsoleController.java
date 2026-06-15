package com.example.banking.console;

import com.example.banking.console.handler.AccountOperationHandler;
import com.example.banking.console.handler.UserOperationHandler;
import com.example.banking.service.OperationsConsoleListener;
import org.springframework.stereotype.Component;

@Component
public class ConsoleController {

    private final ConsoleMenu consoleMenu;
    private final OperationsConsoleListener consoleListener;
    private final UserOperationHandler userHandler;
    private final AccountOperationHandler accountHandler;

    public ConsoleController(ConsoleMenu consoleMenu,
                             OperationsConsoleListener consoleListener,
                             UserOperationHandler userHandler,
                             AccountOperationHandler accountHandler) {
        this.consoleMenu = consoleMenu;
        this.consoleListener = consoleListener;
        this.userHandler = userHandler;
        this.accountHandler = accountHandler;
    }

    public void start() {
        consoleMenu.displayInfo("Welcome to Banking Application!");

        while (true) {
            try {
            consoleMenu.displayMainMenu();
            String choice = consoleListener.readLine("\nPlease select an operation: ");

            if (choice == null || choice.trim().isEmpty()) {
                consoleMenu.displayWarning("Please enter a value");
                continue;
            }

            choice = choice.trim();
            ConsoleOperation operation = ConsoleOperation.fromCodeOrName(choice);

            if (operation == null) {
                consoleMenu.displayError("Invalid choice. Please enter a number (0-8) or command name");
                continue;
            }

            executeOperation(operation);
            } catch (Exception e) {
                // Глобальная обработка ошибок
                System.err.println();
                System.err.printf("❌ System error: %s%n", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void executeOperation(ConsoleOperation operation) {
        switch (operation) {
            case USER_CREATE:
                userHandler.createNewUser();
                break;
            case SHOW_ALL_USERS:
                userHandler.showAllUsers();
                break;
            case SHOW_USER_ACCOUNTS:
                userHandler.showUserAccounts();
                break;
            case ACCOUNT_CREATE:
                accountHandler.createNewAccount();
                break;
            case ACCOUNT_CLOSE:
                accountHandler.closeAccount();
                break;
            case ACCOUNT_DEPOSIT:
                accountHandler.deposit();
                break;
            case ACCOUNT_TRANSFER:
                accountHandler.transfer();
                break;
            case ACCOUNT_WITHDRAW:
                accountHandler.withdraw();
                break;
            default:
                consoleMenu.displayWarning("Operation not yet implemented");
        }
    }
}