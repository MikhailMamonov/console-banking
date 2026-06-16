package com.example.banking.service;

import org.springframework.stereotype.Service;

import java.util.Scanner;

/**
 * Service for handling console input operations.
 * This service is independent and does not depend on commands.
 */
@Service
public class ConsoleInputService {

    private final Scanner scanner = new Scanner(System.in);

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    public void close() {
        scanner.close();
    }
}