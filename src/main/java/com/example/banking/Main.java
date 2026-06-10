package com.example.banking;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.example.banking")
public class Main {
    public static void main(String[] args) {
        // Запуск чистого IoC-контейнера Spring Core
        try (var context = new AnnotationConfigApplicationContext(Main.class)) {

            // Получаем бин из контекста
            GreetingService service = context.getBean(GreetingService.class);

            // Проверяем работу
            System.out.println(service.getGreeting());
        }
    }
}