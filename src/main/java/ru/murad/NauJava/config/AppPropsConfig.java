package ru.murad.NauJava.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppPropsConfig {

    @Value("${app.name}")
    private String name;

    @Value("${app.version}")
    private String version;

    @PostConstruct
    public void printAppInfo() {
        System.out.println("--------------------------------");
        System.out.println("Приложение: " + name);
        System.out.println("Версия: " + version);
        System.out.println("--------------------------------");
    }
}