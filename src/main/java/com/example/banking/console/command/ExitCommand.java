package com.example.banking.console.command;

import com.example.banking.console.ConsoleOperation;
import com.example.banking.service.ConsoleInputService;
import com.example.banking.service.OperationsConsoleListener;
import org.springframework.stereotype.Component;

@Component
public class ExitCommand extends BaseCommand {

    public ExitCommand(ConsoleInputService consoleInput) {
        super(consoleInput);
    }

    @Override
    public void execute() {
        printSeparator();
        System.out.println("Goodbye!");
        System.exit(0);
    }

    @Override
    public ConsoleOperation getOperationType() {
        return ConsoleOperation.EXIT;
    }
}