package com.example.banking.console;

public enum ConsoleOperation {
    USER_CREATE("1", "USER_CREATE", "Создать нового пользователя"),
    SHOW_ALL_USERS("2", "SHOW_ALL_USERS", "Показать всех пользователей"),
    SHOW_USER_ACCOUNTS("3", "SHOW_USER_ACCOUNTS", "Показать счета пользователя"),
    ACCOUNT_CREATE("4", "ACCOUNT_CREATE", "Создать аккаунт"),
    ACCOUNT_CLOSE("5", "ACCOUNT_CLOSE", "Закрыть аккаунт"),
    ACCOUNT_DEPOSIT("6", "ACCOUNT_DEPOSIT", "Пополнить счет"),
    ACCOUNT_TRANSFER("7", "ACCOUNT_TRANSFER", "Перевести на другой счёт"),
    ACCOUNT_WITHDRAW("8", "ACCOUNT_WITHDRAW", "Вывести средства"),
    EXIT("0", "EXIT", "Выход");

    private final String code;
    private final String name;
    private final String description;
    ConsoleOperation(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    public static ConsoleOperation fromCode(String code) {
        for (ConsoleOperation op : values()) {
            if (op.code.equals(code)) {
                return op;
            }
        }
        return null;
    }

    public static ConsoleOperation fromName(String name) {
        for (ConsoleOperation op : values()) {
            if (op.name.equalsIgnoreCase(name)) {
                return op;
            }
        }
        return null;
    }

    public static ConsoleOperation fromCodeOrName(String input) {
        // Сначала пробуем как код (цифру)
        ConsoleOperation operation = fromCode(input);

        // Если не нашли, пробуем как имя (текст)
        if (operation == null) {
            operation = fromName(input);
        }

        return operation;
    }
}