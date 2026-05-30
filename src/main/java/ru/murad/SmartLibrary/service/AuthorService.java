package ru.murad.SmartLibrary.service;

/**
 * Интерфейс сервиса управления авторами в библиотечной системе.
 */
public interface AuthorService {
    /**
     * Удаляет автора и все его книги из каталога.
     *
     * @param authorId уникальный идентификатор автора
     */
    void deleteAuthorWithBooks(Long authorId);
}
