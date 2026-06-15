package com.example.banking.service;

import com.example.banking.model.entity.Account;
import com.example.banking.model.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserAccountService {

    private final UserService userService;
    private final AccountService accountService;

    public UserAccountService(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    public User createUserWithAccount(String login, double initialBalance) {
        User user = userService.createUser(login);
        Account account = accountService.createAccount(user.getId(), initialBalance);

        user.getAccountList().clear();

        user.getAccountList().add(account);

        if (user.getAccountList().size() != 1) {
            throw new IllegalStateException("Account list size is " +
                    user.getAccountList().size() + ", expected 1");
        }

        return user;
    }

}