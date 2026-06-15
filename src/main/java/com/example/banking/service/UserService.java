package com.example.banking.service;

import com.example.banking.exception.BankingException;
import com.example.banking.exception.ErrorType;
import com.example.banking.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления пользователями банковского приложения.
 * Предоставляет операции по созданию, поиску, обновлению и удалению пользователей.
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
public class UserService {
    private final List<User> users = new ArrayList<>();
    private final AccountLogger accountLogger;
    private final IdGeneratorService idGeneratorService;

<<<<<<< Updated upstream
    public User createUser(String id, String login, List<Account> accounts) {
        User user = new User(id, login, accounts);
        users.add(user);
        System.out.println("Пользователь " + login + " успешно создан!");
=======
    /**
     * Конструктор сервиса пользователей.
     *
     * @param accountLogger логгер для записи операций с пользователями
     * @param idGeneratorService сервис для генерации уникальных ID пользователей
     */
    public UserService(AccountLogger accountLogger,IdGeneratorService idGeneratorService) {
        this.accountLogger = accountLogger;
        this.idGeneratorService = idGeneratorService;
    }

    /**
     * Проверяет, существует ли пользователь с указанным логином.
     *
     * @param login логин пользователя для проверки
     * @return {@code true} если пользователь с таким логином уже существует,
     *         {@code false} в противном случае
     * @throws IllegalArgumentException если логин равен null или пустой строке
     */
    public boolean isLoginExists(String login) {
        return findUserByLogin(login).isPresent();
    }

    /**
     * Создает нового пользователя без счетов
     * @param login логин пользователя
     * @return созданный пользователь
     * @throws BankingException если логин некорректен или уже существует
     */
    public User createUser(String login) {
        validateLogin(login);

        String id = idGeneratorService.generateUserId();

        User user = new User(id, login, new ArrayList<>());
        users.add(user);

        accountLogger.logUserCreated(user);

>>>>>>> Stashed changes
        return user;
    }

    /**
     * Находит пользователя по ID или выбрасывает исключение
     * @param userId ID пользователя
     * @return найденный пользователь
     * @throws BankingException если пользователь не найден
     */
    public User findUserOrThrow(String userId) {
        return findUserById(userId)
                .orElseThrow(() -> new BankingException("USER_OPERATION", ErrorType.USER_NOT_FOUND,
                        String.format("User with ID '%s' not found", userId)));
    }

    private void validateLogin(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Login cannot be null or empty");
        }

        if (findUserByLogin(login).isPresent()) {
            throw new IllegalArgumentException("User with login '" + login + "' already exists");
        }
    }

    /**
     * Возвращает список всех пользователей.
     *
     * <p>Возвращается копия списка, чтобы защитить внутренние данные от изменений.
     *
     * @return новый ArrayList со всеми пользователями
     *         (пустой список, если пользователей нет)
     *
     * @example
     * <pre>
     * List<User> allUsers = userService.getAllUsers();
     * for (User user : allUsers) {
     *     System.out.println(user.getLogin());
     * }
     * </pre>
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Находит пользователя по ID.
     *
     * <p>Возвращает Optional для безопасной обработки случая, когда пользователь не найден.
     *
     * @param id идентификатор пользователя
     * @return Optional содержащий пользователя, если найден, иначе пустой Optional
     *
     * @example
     * <pre>
     * // Безопасный поиск
     * userService.findUserById("123")
     *     .ifPresent(user -> System.out.println(user.getLogin()));
     *
     * // С проверкой
     * Optional&lt;User&gt; user = userService.findUserById("123");
     * if (user.isPresent()) {
     *     System.out.println(user.get().getLogin());
     * } else {
     *     System.out.println("User not found");
     * }
     * </pre>
     */
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

    /**
     * Проверяет существование пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @return {@code true} если пользователь существует, {@code false} в противном случае
     *
     * @example
     * <pre>
     * if (userService.userExists("123")) {
     *     System.out.println("User exists");
     * } else {
     *     System.out.println("User does not exist");
     * }
     * </pre>
     */
    public boolean userExists(String id) {
        return users.stream().anyMatch(user -> user.getId().equals(id));
    }

    public void showAllUsers() {
        accountLogger.logAllUsers(users);
    }

    public boolean deleteUser(String userId) {
        Optional<User> userToDelete = findUserById(userId);
        if (userToDelete.isPresent()) {
            users.remove(userToDelete.get());
            accountLogger.logUserDeleted(userToDelete.get());
            return true;
        } else {
<<<<<<< Updated upstream
            System.out.println("\n=== СПИСОК ПОЛЬЗОВАТЕЛЕЙ ===");
            users.forEach(user -> {
                System.out.println("ID: " + user.getId() + ", Логин: " + user.getLogin());
                System.out.println("  Аккаунтов: " + user.getAccountList().size());
            });
=======
            accountLogger.logUserNotFound(userId);
            return false;
>>>>>>> Stashed changes
        }
    }

    public Optional<User> updateUserLogin(String userId, String newLogin) {
        if (newLogin == null || newLogin.trim().isEmpty()) {
            throw new BankingException("USER_UPDATE", ErrorType.VALIDATION_ERROR,
                    "New login cannot be null or empty");
        }

        if (!newLogin.equals(findUserById(userId).get().getLogin())
                && isLoginExists(newLogin)) {
            throw new BankingException("USER_UPDATE", ErrorType.VALIDATION_ERROR,
                    String.format("Login '%s' is already taken", newLogin));
        }

        return findUserById(userId).map(user -> {
            String oldLogin = user.getLogin();
            user.setLogin(newLogin);
            accountLogger.logUserLoginUpdated(oldLogin, newLogin);
            return user;
        });
    }

    public int getTotalUserCount() {
        return users.size();
    }

    public void clearAllUsers() {
        int count = users.size();
        users.clear();
        accountLogger.logAllUsersCleared(count);
    }


}
