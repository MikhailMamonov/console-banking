package com.example.banking.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class BankingException extends RuntimeException {
    private final String operationType;
    private final ErrorType errorType;
    private final String errorCode;
    private final Object[] details;
    private final LocalDateTime timestamp;
    private final String userId;
    private final String accountId;

    // ========== КОНСТРУКТОРЫ ==========

    // Базовый конструктор
    public BankingException(String operationType, String message) {
        this(operationType, ErrorType.BUSINESS_ERROR, null, message, null, null, null);
    }

    // С типом ошибки
    public BankingException(String operationType, ErrorType errorType, String message) {
        this(operationType, errorType, null, message, null, null, null);
    }

    // С типом ошибки и деталями
    public BankingException(String operationType, ErrorType errorType, String message, Object... details) {
        this(operationType, errorType, null, message, null, null, details);
    }

    // С кодом ошибки
    public BankingException(String operationType, ErrorType errorType, String errorCode, String message) {
        this(operationType, errorType, errorCode, message, null, null, null);
    }

    // Полный конструктор
    public BankingException(String operationType, ErrorType errorType, String errorCode,
                            String message, String userId, String accountId, Object... details) {
        super(message);
        this.operationType = operationType;
        this.errorType = errorType != null ? errorType : ErrorType.BUSINESS_ERROR;
        this.errorCode = errorCode != null ? errorCode : generateErrorCode(operationType, this.errorType);
        this.userId = userId;
        this.accountId = accountId;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    // ========== ГЕНЕРАЦИЯ КОДА ОШИБКИ ==========

    private String generateErrorCode(String operationType, ErrorType errorType) {
        String opPrefix = operationType.length() > 3 ? operationType.substring(0, 3) : operationType;
        String errPrefix = errorType.name().length() > 3 ? errorType.name().substring(0, 3) : errorType.name();
        long timestamp = System.currentTimeMillis() % 10000;
        return String.format("%s_%s_%04d", opPrefix.toUpperCase(), errPrefix.toUpperCase(), timestamp);
    }

    // ========== ГЕТТЕРЫ ==========

    public String getOperationType() {
        return operationType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object[] getDetails() {
        return details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getAccountId() {
        return accountId;
    }

    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========

    /**
     * Получить дружественное сообщение для пользователя
     */
    public String getUserFriendlyMessage() {
        switch (errorType) {
            case INSUFFICIENT_FUNDS:
                return "Недостаточно средств на счете";
            case ACCOUNT_NOT_FOUND:
                return "Счет не найден";
            case USER_NOT_FOUND:
                return "Пользователь не найден";
            case VALIDATION_ERROR:
                return "Ошибка валидации: " + getMessage();
            case TRANSFER_ERROR:
                return "Ошибка при переводе средств";
            case CLOSING_LAST_ACCOUNT:
                return "Нельзя закрыть единственный счет";
            case NEGATIVE_BALANCE:
                return "Нельзя закрыть счет с отрицательным балансом";
            case SAME_ACCOUNT_TRANSFER:
                return "Нельзя перевести средства на тот же счет";
            case CONFIGURATION_ERROR:
                return "Ошибка конфигурации приложения";
            case CREATE_ACCOUNT_ERROR:
                return "Ошибка при создании счета";
            case CLOSE_ACCOUNT_ERROR:
                return "Ошибка при закрытии счета";
            default:
                return getMessage();
        }
    }

    /**
     * Получить детали ошибки в виде строки
     */
    public String getDetailsAsString() {
        if (details == null || details.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < details.length; i += 2) {
            if (i + 1 < details.length) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(String.format("%s=%s", details[i], details[i + 1]));
            }
        }
        return sb.toString();
    }

    /**
     * Проверить, содержит ли исключение определенный тип ошибки
     */
    public boolean isErrorType(ErrorType type) {
        return this.errorType == type;
    }

    /**
     * Проверить, является ли ошибка критической (требует остановки операции)
     */
    public boolean isCritical() {
        return errorType == ErrorType.CONFIGURATION_ERROR ||
                errorType == ErrorType.NEGATIVE_BALANCE;
    }

    // ========== ПЕРЕОПРЕДЕЛЕННЫЕ МЕТОДЫ ==========

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("BankingException{code='%s', type='%s', operation='%s', message='%s'",
                errorCode, errorType, operationType, getMessage()));

        if (userId != null) {
            sb.append(String.format(", userId='%s'", userId));
        }
        if (accountId != null) {
            sb.append(String.format(", accountId='%s'", accountId));
        }
        if (details != null && details.length > 0) {
            sb.append(String.format(", details=%s", Arrays.toString(details)));
        }
        sb.append(String.format(", timestamp=%s}",
                timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));

        return sb.toString();
    }
}