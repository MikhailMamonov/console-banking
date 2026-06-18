package com.example.banking;

import com.example.banking.config.SpringConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public abstract class BaseIntegrationTest {

    protected AnnotationConfigApplicationContext context;

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    private ByteArrayOutputStream testOut;

    @BeforeEach
    void setUp() {
        // Поднимаем реальный контекст Spring со всеми сервисами
        context = new AnnotationConfigApplicationContext(SpringConfig.class);

        // Перехватываем стандартный вывод консоли
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
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
        return testOut.toString();
    }
}