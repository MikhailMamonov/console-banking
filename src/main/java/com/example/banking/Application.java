package com.example.banking;

import com.example.banking.config.SpringConfig;
import com.example.banking.service.ConsoleInputService;
import com.example.banking.service.OperationsConsoleListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(SpringConfig.class)) {

            OperationsConsoleListener consoleListener =
                    context.getBean(OperationsConsoleListener.class);
            ConsoleInputService consoleInput =
                    context.getBean(ConsoleInputService.class);

            System.out.println("Welcome to the Banking Application!");
            System.out.println("Type 'EXIT' or '0' to quit.\n");

            while (true) {
                consoleListener.printMenu();
                String choice = consoleInput.readLine("\nPlease enter operation type: ");

                if ("0".equals(choice) || "EXIT".equalsIgnoreCase(choice)) {
                    System.out.println("Goodbye!");
                    break;
                }

                consoleListener.processOperation(choice);
                System.out.println("\nPress Enter to continue...");
                consoleInput.readLine("");
            }

            consoleInput.close();
        }
    }
}