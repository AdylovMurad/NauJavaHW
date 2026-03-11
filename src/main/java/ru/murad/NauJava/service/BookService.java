package ru.murad.NauJava.service;

import ru.murad.NauJava.entity.Book;
import java.util.List;

public interface BookService {
    void createBook(Long id, String title, String author);
    Book findById(Long id);
    void deleteById(Long id);
    void updateBook(Long id, String newTitle, String newAuthor);
    List<Book> getAllBooks();
}