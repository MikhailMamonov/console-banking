package com.example.banking.runner;

import com.example.banking.service.OperationsConsoleListener;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * Runner для запуска бесконечного цикла консольного меню банковского приложения.
 */
@Component
public class ConsoleRunner implements CommandLineRunner {

    private final OperationsConsoleListener consoleListener;
    private final Scanner scanner;

    public ConsoleRunner(OperationsConsoleListener consoleListener, Scanner scanner) {
        this.consoleListener = consoleListener;
        this.scanner = scanner;
    }

    @Override
    public void run(String... args) throws Exception {
        boolean running = true;

        System.out.println("Welcome to the Banking Application!");

        while (running) {
            // 1. Выводим доступное меню команд
            consoleListener.printMenu();
            System.out.print("Enter operation name (or type 'EXIT' to quit): ");

            // 2. Считываем ввод пользователя
            String input = scanner.nextLine().trim();

            // 3. Проверяем команду на выход из приложения
            if ("EXIT".equalsIgnoreCase(input)) {
                System.out.println("Exiting the application. Goodbye!");
                running = false;
                continue;
            }

            // 4. Передаем команду в сервис для обработки
            try {
                consoleListener.processOperation(input);
            } catch (Exception e) {
                System.out.println("An error occurred during operation: " + e.getMessage());
            }
        }
    }
}