package ru.murad.NauJava.service;

import ru.murad.NauJava.entity.Book;
import java.util.List;
import java.util.Optional;

public interface BookService {
    void createBook(Long id, String title, String authorName);
    List<Book> getAllBooks();
    void deleteBook(Long id);
    void updateBook(Long id, String title, String authorName);
    Optional<Book> findById(Long id);
}