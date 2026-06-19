package com.example.banking;

import com.example.banking.config.SpringConfig;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.UserRepository;
import com.example.banking.service.AccountService;
import com.example.banking.service.OperationsConsoleListener;
import com.example.banking.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public abstract class BaseIntegrationTest {

    protected AnnotationConfigApplicationContext context;
    protected UserService userService;
    protected AccountService accountService;
    protected OperationsConsoleListener consoleListener;
    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        // Инициализация Spring контекста
        if (context == null || !context.isActive()) {
            context = new AnnotationConfigApplicationContext(BankingApplication.class);
        }

        // Получение бинов из контекста
        userService = context.getBean(UserService.class);
        accountService = context.getBean(AccountService.class);
        consoleListener = context.getBean(OperationsConsoleListener.class);

        // Перенаправление вывода для тестирования
        System.setOut(new PrintStream(outputStream));
    }

    protected void setupSpringContext() {
        context = new AnnotationConfigApplicationContext(SpringConfig.class);

        // Чистим базу данных через EntityManager
        var entityManagerFactory = context.getBean(jakarta.persistence.EntityManagerFactory.class);
        var em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("TRUNCATE TABLE accounts, users RESTART IDENTITY CASCADE").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @AfterEach
    void tearDown() {
        // Возвращаем стандартные потоки и закрываем контекст
        System.setIn(originalIn);
        System.setOut(originalOut);
        if (context != null) {
            context.close();
        }
    }

    /**
     * Имитирует последовательный ввод строк пользователем в консоль
     */
    protected void provideInput(String... lines) {
        String input = String.join(System.lineSeparator(), lines) + System.lineSeparator();
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }

    /**
     * Возвращает всё, что приложение успело написать в консоль
     */
    protected String getOutput() {
        return outputStream.toString();
    }

    protected void clearOutput() {
        outputStream.reset();
    }
}