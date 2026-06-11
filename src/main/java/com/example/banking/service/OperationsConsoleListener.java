package com.example.banking.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Scanner;


@Service
public class OperationsConsoleListener {
    private final Scanner scanner = new Scanner(System.in);

    public String readLine(String prompt){
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public double readDouble(String prompt) {
        System.out.print(prompt);
        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите число");
            return readDouble(prompt);
        }
    }

    public int readInt(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите число");
            return readInt(prompt);
        }
    }


}
