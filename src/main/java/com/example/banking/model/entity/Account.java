package com.example.banking.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "accounts")
public class Account {

    @Id // Первичный ключ
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "money_amount")
    private double moneyAmount;

    public Account() {
    }

    public Account(String id, String userId, double moneyAmount) {
        this.id = id;
        this.userId = userId;
        this.moneyAmount = moneyAmount;
    }

    // Ваши геттеры, сеттеры и toString() остаются без изменений
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public double getMoneyAmount() { return moneyAmount; }
    public void setMoneyAmount(double moneyAmount) { this.moneyAmount = moneyAmount; }

    @Override
    public String toString() {
        return String.format("Account{id=%s, userId=%s, moneyAmount=%.2f}", id, userId, moneyAmount);
    }
}