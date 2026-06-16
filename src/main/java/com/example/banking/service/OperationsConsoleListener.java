package com.example.banking.service;

import com.example.banking.console.ConsoleOperation;
import com.example.banking.console.OperationCommand;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for handling console operations.
 * Processes commands and manages the command map.
 */
@Component
public class OperationsConsoleListener {

    private final Map<ConsoleOperation, OperationCommand> commandMap = new HashMap<>();

    public OperationsConsoleListener(List<OperationCommand> commands) {
        commands.forEach(command ->
                commandMap.put(command.getOperationType(), command)
        );
    }

    public void processOperation(String operationType) {
        ConsoleOperation type = ConsoleOperation.fromString(operationType);
        if (type == null) {
            System.out.println("Invalid operation type. Please try again.");
            return;
        }

        OperationCommand command = commandMap.get(type);
        if (command == null) {
            System.out.println("Command not found for operation: " + operationType);
            return;
        }

        command.execute();
    }

    public void printMenu() {
        System.out.println("\n╔═══════════════════════════════════════════╗");
        System.out.println("║         BANKING APPLICATION             ║");
        System.out.println("╠═══════════════════════════════════════════╣");
        for (ConsoleOperation type : ConsoleOperation.values()) {
            System.out.printf("║ %-15s - %-26s ║%n",
                    type.name(), type.getDescription());
        }
        System.out.println("╚═══════════════════════════════════════════╝");
    }
}