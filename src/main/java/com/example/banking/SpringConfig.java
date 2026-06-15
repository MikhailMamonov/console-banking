package com.example.banking;

import com.example.banking.service.AccountService;
import com.example.banking.service.OperationsConsoleListener;
import com.example.banking.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.example.banking")
public class SpringConfig {

    @Bean
    public OperationsConsoleListener operationsConsoleListener() {
        return new OperationsConsoleListener();
    }
}