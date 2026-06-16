package com.example.banking.console;

/**
 * Command interface for console operations.
 * Each operation type should have its own implementation.
 */
public interface OperationCommand {

    /**
     * Executes the command logic.
     */
    void execute();

    /**
     * Returns the operation type this command handles.
     *
     * @return the ConsoleOperationType
     */
    ConsoleOperation getOperationType();
}