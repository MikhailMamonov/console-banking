package com.example.banking.service;

import com.example.banking.exception.BankingException;
import com.example.banking.exception.ErrorType;
import com.example.banking.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of UserService for managing bank application users.
 * Provides operations for creating, searching, updating, and deleting users.
 *
 * <p>Основные возможности:
 * <ul>
 *   <li>Создание новых пользователей с уникальным логином</li>
 *   <li>Поиск пользователей по ID или логину</li>
 *   <li>Обновление данных пользователя</li>
 *   <li>Удаление пользователей</li>
 *   <li>Просмотр всех пользователей</li>
 * </ul>
 *
 * @author Your Name
 * @version 1.0
 * @see User
 * @see AccountLogger
 */
@Service
public class UserServiceImpl implements UserService {

    private final List<User> users = new ArrayList<>();
    private final AccountLogger accountLogger;
    private final UserValidator userValidator;
    private final AtomicLong idCounter = new AtomicLong(100000);

    /**
     * Конструктор сервиса пользователей.
     *
     * @param accountLogger логгер для записи операций с пользователями
     * @param userValidator сервис для валидации пользователей
     */
    public UserServiceImpl(AccountLogger accountLogger, UserValidator userValidator) {
        this.accountLogger = accountLogger;
        this.userValidator = userValidator;
    }

    // === Public Methods ===

    @Override
    public boolean isLoginExists(String login) {
        return findUserByLogin(login).isPresent();
    }

    @Override
    public User createUser(String login) {
        userValidator.validateLoginFormat(login);
        userValidator.validateLoginUniqueness(login, isLoginExists(login));

        String userId = "USR-" + idCounter.incrementAndGet();
        User user = new User(userId, login, new ArrayList<>());
        users.add(user);

        accountLogger.logUserCreated(user);

        return user;
    }

    @Override
    public User findUserOrThrow(String userId) {
        return findUserById(userId)
                .orElseThrow(() -> new BankingException("USER_OPERATION", ErrorType.USER_NOT_FOUND,
                        String.format("User with ID '%s' not found", userId)));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public Optional<User> findUserById(String id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<User> findUserByLogin(String login) {
        return users.stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst();
    }

    @Override
    public boolean userExists(String id) {
        return users.stream().anyMatch(user -> user.getId().equals(id));
    }

    @Override
    public void showAllUsers() {
        accountLogger.logAllUsers(users);
    }

    @Override
    public boolean deleteUser(String userId) {
        Optional<User> userToDelete = findUserById(userId);
        if (userToDelete.isPresent()) {
            users.remove(userToDelete.get());
            accountLogger.logUserDeleted(userToDelete.get());
            return true;
        } else {
            accountLogger.logUserNotFound(userId);
            return false;
        }
    }

    @Override
    public Optional<User> updateUserLogin(String userId, String newLogin) {
        userValidator.validateNotEmpty(newLogin, "New login", "USER_UPDATE");

        Optional<User> userOpt = findUserById(userId);
        User user = userValidator.validateUserPresent(userOpt, userId, "USER_UPDATE");

        userValidator.validateLoginAvailabilityForUpdate(newLogin, user.getLogin(), isLoginExists(newLogin));

        String oldLogin = user.getLogin();
        user.setLogin(newLogin);
        accountLogger.logUserLoginUpdated(oldLogin, newLogin);

        return Optional.of(user);
    }

    @Override
    public int getTotalUserCount() {
        return users.size();
    }

    @Override
    public void clearAllUsers() {
        int count = users.size();
        users.clear();
        accountLogger.logAllUsersCleared(count);
    }
}