package com.example.banking.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class IdGeneratorService {

    private final AtomicLong userIdGenerator = new AtomicLong(1);
    private final AtomicLong accountIdGenerator = new AtomicLong(1);

    public String generateUserId() {
        return String.valueOf(userIdGenerator.getAndIncrement());
    }

    public String generateAccountId() {
        return String.valueOf(accountIdGenerator.getAndIncrement());
    }

    // Для тестов
    public void reset() {
        userIdGenerator.set(1);
        accountIdGenerator.set(1);
    }
}