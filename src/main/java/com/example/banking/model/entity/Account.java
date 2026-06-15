package com.example.banking.model.entity;

import org.springframework.stereotype.Component;

import java.util.List;


public class Account {
<<<<<<< Updated upstream
    private String id;
    private String userId;
    private double moneyAmount;

=======

    private final String id;
    private String userId;
    private double moneyAmount;

    public Account(String userId, double moneyAmount, String id) {

        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("UserId cannot be null or empty");
        }

        if (moneyAmount <= 0) {
            throw new IllegalArgumentException("The amount must be greater than 0");
        }

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
>>>>>>> Stashed changes

        this.id = id;
        this.userId = userId;
        this.moneyAmount = moneyAmount;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public double getMoneyAmount() {
        return moneyAmount;
    }

    public void setMoneyAmount(double moneyAmount) {
        this.moneyAmount = moneyAmount;
    }
}
