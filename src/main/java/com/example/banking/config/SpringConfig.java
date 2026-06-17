package com.example.banking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Scanner;

/**
 * Spring configuration class for the banking application.
 * Uses component scanning to discover beans automatically.
 */
@Configuration
@ComponentScan(basePackages = "com.example.banking")
@PropertySource("classpath:application.properties")
public class SpringConfig {
    // All beans are automatically discovered via @ComponentScan
    // No need to explicitly declare beans with @Bean
    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }
}