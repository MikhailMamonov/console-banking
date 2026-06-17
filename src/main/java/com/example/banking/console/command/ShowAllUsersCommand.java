package com.example.banking.console.command;

import com.example.banking.console.ConsoleOperation;
import com.example.banking.service.ConsoleInputService;
import com.example.banking.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class ShowAllUsersCommand extends BaseCommand {

    private final UserService userService;

    public ShowAllUsersCommand(ConsoleInputService consoleInput,
                               UserService userService) {
        super(consoleInput);
        this.userService = userService;
    }

    @Override
    public void execute() {
        printSeparator();
        printInfo("Showing all users");
        userService.showAllUsers();
    }

    @Override
    public ConsoleOperation getOperationType() {
        return ConsoleOperation.SHOW_ALL_USERS;
    }
}