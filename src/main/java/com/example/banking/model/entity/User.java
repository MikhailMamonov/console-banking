package com.example.banking.model.entity;

import java.util.List;

public class User {
    private String id;
    private String login;
    private List<Account> accountList;

    public User(String id, String login, List<Account> accountList) {
        this.id = id;
        this.login = login;
        this.accountList = accountList;
    }

    public String getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\nUser{id=%s, login='%s'\n", id, login));
        sb.append("  Accounts:\n");

        if (accountList == null || accountList.isEmpty()) {
            sb.append("    └─ No accounts\n");
        } else {
            for (Account account : accountList) {
                sb.append(String.format("    ├─ ID: %s | Balance: %.2f\n",
                        account.getId(), account.getMoneyAmount()));
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
