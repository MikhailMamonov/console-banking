package com.example.banking.model.entity;


public class Account {

    private String id;
    private String userId;
    private double moneyAmount;

    public Account(String id, String userId, double moneyAmount) {
        this.id = id;
        this.userId = userId;
        this.moneyAmount = moneyAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getMoneyAmount() {
        return moneyAmount;
    }

    public void setMoneyAmount(double moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return String.format("Account{id=%s, userId=%s, moneyAmount=%.2f}",
                id, userId, moneyAmount);
    }
}