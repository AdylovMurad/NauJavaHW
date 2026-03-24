package ru.murad.NauJava.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Scanner;

@Configuration
public class ConsoleConfig {

    @Autowired
    private CommandProcessor commandProcessor;

    @Bean
    public CommandLineRunner commandScanner() {
        return args -> {
            Thread consoleThread = new Thread(() -> {
                try (Scanner scanner = new Scanner(System.in)) {
                    System.out.println("=== Библиотечная система запущена ===");
                    System.out.println("Введите команду (например: create 1 \"Война и Мир\" \"Лев Толстой\" ). exit для выхода.");
                    System.out.println("Доступные команды: \ncreate, \nread, \nupdate, \nlist, \ndelete, \nexit");

                    while (true) {
                        System.out.print("> ");
                        if (!scanner.hasNextLine()) break;

                        String input = scanner.nextLine().trim();
                        if ("exit".equalsIgnoreCase(input)) {
                            System.out.println("Завершение работы...");
                            System.exit(0);
                        }

                        if (!input.isEmpty()) {
                            commandProcessor.processCommand(input);
                        }
                    }
                }
            });
            consoleThread.setDaemon(true);
            consoleThread.start();
        };
    }
}
