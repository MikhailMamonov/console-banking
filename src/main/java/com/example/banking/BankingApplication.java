package com.example.banking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;



@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class BankingApplication {
    public static void main(String[] args) {
        // Spring Boot запускает контекст, находит ConsoleRunner и выполняет метод run()
        SpringApplication.run(BankingApplication.class, args);
    }
}