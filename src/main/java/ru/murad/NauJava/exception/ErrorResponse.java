package ru.murad.NauJava.exception;

public class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public static ErrorResponse create(Throwable e) {
        return new ErrorResponse(e.getMessage());
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
