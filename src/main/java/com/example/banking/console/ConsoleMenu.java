package com.example.banking.console;

import org.springframework.stereotype.Component;

@Component
public class ConsoleMenu {

    public void displayMainMenu() {
        System.out.println("\n=== БАНКОВСКОЕ ПРИЛОЖЕНИЕ ===");
        for (ConsoleOperation op : ConsoleOperation.values()) {
            System.out.printf("%s. %s%n", op.getName(), op.getDescription());
        }
    }

    public void displaySectionHeader(String title) {
        System.out.printf("%n=== %s ===%n", title);
    }

    public void displaySuccess(String message, Object... args) {
        System.out.printf("✅ " + message + "%n", args);
    }

    public void displayError(String message, Object... args) {
        System.err.printf("❌ " + message + "%n", args);
    }

    public void displayInfo(String message, Object... args) {
        System.out.printf("ℹ️ " + message + "%n", args);
    }

    public void displayWarning(String message, Object... args) {
        System.out.printf("⚠️ " + message + "%n", args);
    }
}