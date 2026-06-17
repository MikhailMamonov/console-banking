package com.example.banking.repository;

import com.example.banking.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    // Поиск всех аккаунтов конкретного пользователя
    List<Account> findByUserId(String userId);
}