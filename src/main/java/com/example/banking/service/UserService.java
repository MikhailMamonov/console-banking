package com.example.banking.service;

import com.example.banking.model.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing bank application users.
 * Provides operations for creating, searching, updating, and deleting users.
 *
 * @author Your Name
 * @version 1.0
 * @see User
 */
public interface UserService {

    /**
     * Checks if a user with the specified login exists.
     *
     * @param login the user login to check
     * @return {@code true} if a user with this login already exists, {@code false} otherwise
     * @throws IllegalArgumentException if login is null or empty
     */
    boolean isLoginExists(String login);

    /**
     * Creates a new user without accounts.
     *
     * @param login the user login
     * @return the created user
     * @throws com.example.banking.exception.BankingException if login is invalid or already exists
     */
    User createUser(String login);

    /**
     * Finds a user by ID or throws an exception.
     *
     * @param userId the user ID
     * @return the found user
     * @throws com.example.banking.exception.BankingException if user is not found
     */
    User findUserOrThrow(String userId);

    /**
     * Returns a list of all users.
     *
     * @return a new ArrayList with all users (empty list if no users)
     */
    List<User> getAllUsers();

    /**
     * Finds a user by ID.
     *
     * @param id the user ID
     * @return Optional containing the user if found, otherwise empty Optional
     */
    Optional<User> findUserById(String id);

    /**
     * Finds a user by login.
     *
     * @param login the user login
     * @return Optional containing the user if found, otherwise empty Optional
     */
    Optional<User> findUserByLogin(String login);

    /**
     * Checks if a user exists by ID.
     *
     * @param id the user ID
     * @return {@code true} if the user exists, {@code false} otherwise
     */
    boolean userExists(String id);

    /**
     * Displays all users using the account logger.
     */
    void showAllUsers();

    /**
     * Deletes a user by ID.
     *
     * @param userId the user ID to delete
     * @return {@code true} if the user was deleted, {@code false} otherwise
     */
    boolean deleteUser(String userId);

    /**
     * Updates a user's login.
     *
     * @param userId the user ID to update
     * @param newLogin the new login
     * @return Optional containing the updated user if successful
     * @throws com.example.banking.exception.BankingException if validation fails
     */
    Optional<User> updateUserLogin(String userId, String newLogin);

    /**
     * Gets the total number of users.
     *
     * @return the total user count
     */
    int getTotalUserCount();

    /**
     * Clears all users.
     */
    void clearAllUsers();
}