package com.example.banking.service;

import com.example.banking.model.entity.Account;
import com.example.banking.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final List<User> users = new ArrayList<>();

    public User createUser(String login, List<Account> accounts) {
        String id = String.valueOf(getAllUsers().size()+1);
        User user = new User(id, login, accounts);
        users.add(user);
        return user;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public Optional<User> findUserById(String id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public Optional<User> findUserByLogin(String login) {
        return users.stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst();
    }

    public boolean userExists(String id) {
        return users.stream().anyMatch(user -> user.getId().equals(id));
    }

    public void showAllUsers() {
        if (users.isEmpty()) {
            System.out.println("Нет зарегистрированных пользователей");
        } else {
            System.out.println("\nList of all users:");
            users.forEach(user -> {
                System.out.println(user);
            });
        }
    }

}
