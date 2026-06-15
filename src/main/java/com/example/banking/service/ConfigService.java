package com.example.banking.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для управления конфигурацией приложения.
 * Централизованное место для доступа к настройкам из application.properties
 */
@Service
public class ConfigService {

    // Account settings
    @Value("${account.default-amount:0.0}")
    private String defaultAmountStr;

    @Value("${account.transfer-commission:0.0}")
    private String transferCommissionStr;

    @Value("${account.minimum-balance:0.0}")
    private String minimumBalanceStr;

    @Value("${account.maximum-balance:1000000.0}")
    private String maximumBalanceStr;

    // User settings
    @Value("${user.max-accounts:5}")
    private String maxAccountsPerUserStr;

    @Value("${user.login.min-length:3}")
    private String minLoginLengthStr;

    @Value("${user.login.max-length:20}")
    private String maxLoginLengthStr;

    // Application settings
    @Value("${app.name:Banking Application}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.enable-logging:true}")
    private boolean enableLogging;

    // Cached values (для производительности)
    private double defaultAmount;
    private double transferCommission;
    private double minimumBalance;
    private double maximumBalance;
    private int maxAccountsPerUser;
    private int minLoginLength;
    private int maxLoginLength;

    // Конфигурация с значениями по умолчанию
    private final Map<String, Object> configCache = new HashMap<>();

    @PostConstruct
    public void init() {
        // Парсим и кешируем значения при старте
        this.defaultAmount = parseDouble(defaultAmountStr, 0.0);
        this.transferCommission = parseDouble(transferCommissionStr, 0.0);
        this.minimumBalance = parseDouble(minimumBalanceStr, 0.0);
        this.maximumBalance = parseDouble(maximumBalanceStr, 1_000_000.0);

        // Сохраняем в кеш
        configCache.put("defaultAmount", this.defaultAmount);
        configCache.put("transferCommission", this.transferCommission);
        configCache.put("minimumBalance", this.minimumBalance);
        configCache.put("maximumBalance", this.maximumBalance);
    }

    // === Геттеры для настроек ===

    public double getDefaultAmount() {
        return defaultAmount;
    }

    public double getTransferCommission() {
        return transferCommission;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

    public double getMaximumBalance() {
        return maximumBalance;
    }

    public int getMaxAccountsPerUser() {
        return maxAccountsPerUser;
    }

    public int getMinLoginLength() {
        return minLoginLength;
    }

    public int getMaxLoginLength() {
        return maxLoginLength;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public boolean isEnableLogging() {
        return enableLogging;
    }

    // === Утилитные методы ===

    /**
     * Получить конфигурацию по ключу
     */
    public Object getConfig(String key) {
        return configCache.get(key);
    }

    /**
     * Проверить, является ли сумма допустимой для транзакции
     */
    public boolean isValidTransactionAmount(double amount) {
        return amount > 0 && amount <= maximumBalance;
    }

    /**
     * Проверить, является ли баланс допустимым
     */
    public boolean isValidBalance(double balance) {
        return balance >= minimumBalance && balance <= maximumBalance;
    }

    /**
     * Получить сообщение с информацией о конфигурации
     */
    public String getConfigInfo() {
        return String.format(
                "Application Configuration:\n" +
                        "  App Name: %s\n" +
                        "  Version: %s\n" +
                        "  Default Amount: %.2f\n" +
                        "  Transfer Commission: %.2f\n" +
                        "  Min Balance: %.2f\n" +
                        "  Max Balance: %.2f\n" +
                        "  Max Accounts per User: %d\n" +
                        "  Min Login Length: %d\n" +
                        "  Max Login Length: %d\n" +
                        "  Logging Enabled: %b",
                appName, appVersion, defaultAmount, transferCommission,
                minimumBalance, maximumBalance, maxAccountsPerUser,
                minLoginLength, maxLoginLength, enableLogging
        );
    }

    // === Приватные методы парсинга ===

    private double parseDouble(String value, double defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private int parseInt(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private boolean parseBoolean(String value, boolean defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
}
