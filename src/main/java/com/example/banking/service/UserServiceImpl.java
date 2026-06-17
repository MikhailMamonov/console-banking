package com.example.banking.service;

import com.example.banking.exception.BankingException;
import com.example.banking.exception.ErrorType;
import com.example.banking.model.entity.User;
import com.example.banking.repository.UserRepository;
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

    private final AccountLogger accountLogger;
    private final UserValidator userValidator;
    private final UserRepository userRepository;

    private final AtomicLong idCounter = new AtomicLong(100000);

    /**
     * Конструктор сервиса пользователей.
     *
     * @param accountLogger логгер для записи операций с пользователями
     * @param userValidator сервис для валидации пользователей
     */
    public UserServiceImpl(AccountLogger accountLogger, UserValidator userValidator, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.accountLogger = accountLogger;
        this.userValidator = userValidator;
    }

    // === Public Methods ===

    @Override
    public boolean isLoginExists(String login) {
        return userRepository.existsByLogin(login);
    }

    @Override
    public User createUser(String login) {
        userValidator.validateLoginFormat(login);
        userValidator.validateLoginUniqueness(login, isLoginExists(login));

        String userId = "USR-" + idCounter.incrementAndGet();
        User user = new User(userId, login, new ArrayList<>());
        User savedUser = userRepository.save(user);

        accountLogger.logUserCreated(savedUser);
        return savedUser;
    }

    @Override
    public User findUserOrThrow(String userId) {
        return userValidator.validateUserPresent(findUserById(userId), userId, "USER_OPERATION");
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    public boolean userExists(String id) {
        return userRepository.existsById(id);
    }

    @Override
    public void showAllUsers() {
        accountLogger.logAllUsers(userRepository.findAll());
    }

    @Override
    public boolean deleteUser(String userId) {
        Optional<User> userToDelete = findUserById(userId);
        if (userToDelete.isPresent()) {
            // Удаляем запись из таблицы PostgreSQL
            userRepository.delete(userToDelete.get());
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
        User updatedUser = userRepository.save(user);
        accountLogger.logUserLoginUpdated(oldLogin, newLogin);

        return Optional.of(updatedUser);
    }

    @Override
    public int getTotalUserCount() {
        // Подсчет строк в таблице через SQL-запрос COUNT(*)
        return (int) userRepository.count();
    }

    @Override
    public void clearAllUsers() {
        int count = getTotalUserCount();
        // Полная очистка таблицы users (и accounts каскадно)
        userRepository.deleteAll();
        accountLogger.logAllUsersCleared(count);
    }
}