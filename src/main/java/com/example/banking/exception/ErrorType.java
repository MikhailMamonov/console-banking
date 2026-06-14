package com.example.banking.exception;

public enum ErrorType {
    // Валидационные ошибки
    VALIDATION_ERROR,

    // Ошибки поиска
    NOT_FOUND,
    USER_NOT_FOUND,
    ACCOUNT_NOT_FOUND,

    // Бизнес-ошибки
    INSUFFICIENT_FUNDS,
    NEGATIVE_BALANCE,
    CLOSING_LAST_ACCOUNT,
    SAME_ACCOUNT_TRANSFER,

    // Операционные ошибки
    TRANSFER_ERROR,
    WITHDRAW_ERROR,
    DEPOSIT_ERROR,
    CLOSE_ACCOUNT_ERROR,
    CREATE_ACCOUNT_ERROR,

    // Конфигурационные ошибки
    CONFIGURATION_ERROR,

    // Общие бизнес-ошибки
    BUSINESS_ERROR
}