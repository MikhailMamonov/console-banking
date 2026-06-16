package com.example.banking.service;

import com.example.banking.exception.BankingException;
import com.example.banking.exception.ErrorType;
import com.example.banking.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
 * @see IdGeneratorService
 */
@Service
public class UserServiceImpl implements UserService {

    private final List<User> users = new ArrayList<>();
    private final AccountLogger accountLogger;
    private final IdGeneratorService idGeneratorService;

    /**
     * Конструктор сервиса пользователей.
     *
     * @param accountLogger логгер для записи операций с пользователями
     * @param idGeneratorService сервис для генерации уникальных ID пользователей
     */
    public UserServiceImpl(AccountLogger accountLogger, IdGeneratorService idGeneratorService) {
        this.accountLogger = accountLogger;
        this.idGeneratorService = idGeneratorService;
    }

    // === Validation Methods ===

    /**
     * Validates the login.
     *
     * @param login the login to validate
     * @throws IllegalArgumentException if login is null, empty, or already exists
     */
    private void validateLogin(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Login cannot be null or empty");
        }

        if (findUserByLogin(login).isPresent()) {
            throw new IllegalArgumentException("User with login '" + login + "' already exists");
        }
    }

    /**
     * Validates that a string is not null or empty.
     *
     * @param value the string to validate
     * @param fieldName the field name for error message
     * @param operationType the operation type for exception
     * @throws BankingException if validation fails
     */
    private void validateNotEmpty(String value, String fieldName, String operationType) {
        if (value == null || value.trim().isEmpty()) {
            throw new BankingException(operationType, ErrorType.VALIDATION_ERROR,
                    String.format("%s cannot be null or empty", fieldName));
        }
    }

    /**
     * Validates a business condition.
     *
     * @param condition the condition to check
     * @param message the error message
     * @param operationType the operation type for exception
     * @throws BankingException if condition is false
     */
    private void validateCondition(boolean condition, String message, String operationType) {
        if (!condition) {
            throw new BankingException(operationType, ErrorType.VALIDATION_ERROR, message);
        }
    }

    // === Public Methods ===

    @Override
    public boolean isLoginExists(String login) {
        return findUserByLogin(login).isPresent();
    }

    @Override
    public User createUser(String login) {
        validateLogin(login);

        String id = idGeneratorService.generateUserId();
        User user = new User(id, login, new ArrayList<>());
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
        validateNotEmpty(newLogin, "New login", "USER_UPDATE");

        Optional<User> userOpt = findUserById(userId);
        if (userOpt.isEmpty()) {
            throw new BankingException("USER_UPDATE", ErrorType.USER_NOT_FOUND,
                    String.format("User with ID '%s' not found", userId));
        }

        User user = userOpt.get();
        if (!newLogin.equals(user.getLogin()) && isLoginExists(newLogin)) {
            throw new BankingException("USER_UPDATE", ErrorType.VALIDATION_ERROR,
                    String.format("Login '%s' is already taken", newLogin));
        }

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