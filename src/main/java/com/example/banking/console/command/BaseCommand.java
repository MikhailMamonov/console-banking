package com.example.banking.console.command;

import com.example.banking.console.OperationCommand;
import com.example.banking.service.ConsoleInputService;

/**
 * Abstract base class for console commands.
 * Provides common functionality for all commands.
 */
public abstract class BaseCommand implements OperationCommand {

    protected final ConsoleInputService consoleInput;

    protected BaseCommand(ConsoleInputService consoleInput) {
        this.consoleInput = consoleInput;
    }

    protected void printSuccess(String message) {
        System.out.println("✅ " + message);
    }

    protected void printError(String message) {
        System.err.println("❌ " + message);
    }

    protected void printInfo(String message) {
        System.out.println("ℹ️ " + message);
    }

    protected void printSeparator() {
        System.out.println("\n" + "=".repeat(50));
    }

    protected String readLine(String prompt) {
        return consoleInput.readLine(prompt);
    }

    protected double readDouble(String prompt) {
        return consoleInput.readDouble(prompt);
    }
}