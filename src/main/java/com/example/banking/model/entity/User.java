package com.example.banking.model.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users") // В Postgres слово user зарезервировано, пишем users
public class User {

    @Id
    private String id;

    @Column(unique = true, nullable = false)
    private String login;

    // Связь один-ко-многим с таблицей аккаунтов
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id") // Связующее поле в таблице accounts
    private List<Account> accountList = new ArrayList<>();

    public User() {
    }

    public User(String id, String login, List<Account> accountList) {
        this.id = id;
        this.login = login;
        this.accountList = accountList != null ? accountList : new ArrayList<>();
    }

    // Ваши геттеры, сеттеры и toString() остаются без изменений
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public List<Account> getAccountList() { return accountList; }
    public void setAccountList(List<Account> accountList) { this.accountList = accountList; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("User{id=%s, login='%s'", id, login));
        sb.append("  Accounts:\n");
        if (accountList == null || accountList.isEmpty()) {
            sb.append("    └─ No accounts\n");
        } else {
            for (Account account : accountList) {
                sb.append(String.format("    ├─ ID: %s | Balance: %.2f\n", account.getId(), account.getMoneyAmount()));
            }
        }
        sb.append("}");
        return sb.toString();
    }
}