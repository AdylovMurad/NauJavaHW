package ru.murad.SmartLibrary.service;

import ru.murad.SmartLibrary.entity.Book;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления книжным фондом библиотеки.
 */
public interface BookService {
    /**
     * Создает новую книгу и автора, если он не существовал.
     *
     * @param id          уникальный идентификатор книги (может быть null)
     * @param title       название книги
     * @param authorName  ФИО автора книги
     */
    void createBook(Long id, String title, String authorName);

    /**
     * Возвращает список всех зарегистрированных книг.
     *
     * @return список книг
     */
    List<Book> getAllBooks();

    /**
     * Удаляет книгу по её идентификатору.
     *
     * @param id идентификатор удаляемой книги
     */
    void deleteBook(Long id);

    /**
     * Обновляет информацию о существующей книге.
     *
     * @param id          идентификатор книги
     * @param title       новое название книги
     * @param authorName  новое ФИО автора
     */
    void updateBook(Long id, String title, String authorName);

    /**
     * Выполняет поиск книги по её идентификатору.
     *
     * @param id идентификатор книги
     * @return Optional, содержащий книгу, если она найдена
     */
    Optional<Book> findById(Long id);
}
