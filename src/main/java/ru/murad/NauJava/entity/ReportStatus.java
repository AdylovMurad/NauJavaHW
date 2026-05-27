package ru.murad.NauJava.entity;

public enum ReportStatus {
    CREATED("создан"),
    COMPLETED("завершен"),
    ERROR("ошибка");

    private final String description;

    ReportStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}