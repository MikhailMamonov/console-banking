package com.example.banking.repository;

import com.example.banking.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // Spring сам поймет по названию метода, что нужно искать пользователя по логину
    Optional<User> findByLogin(String login);

    // Спринг сам сгенерирует SQL-запрос для проверки существования логина
    boolean existsByLogin(String login);
}