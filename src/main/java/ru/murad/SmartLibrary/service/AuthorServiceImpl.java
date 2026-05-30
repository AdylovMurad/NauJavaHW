package ru.murad.SmartLibrary.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.murad.SmartLibrary.entity.Author;
import ru.murad.SmartLibrary.entity.Book;
import ru.murad.SmartLibrary.repository.AuthorRepository;
import ru.murad.SmartLibrary.repository.BookRepository;
import ru.murad.SmartLibrary.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Реализация сервиса управления авторами книг.
 */
@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    /**
     * Конструктор класса AuthorServiceImpl.
     *
     * @param authorRepository репозиторий авторов
     * @param bookRepository   репозиторий книг
     */
    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository,
                             BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public void deleteAuthorWithBooks(Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Автор не найден с ID: " + authorId));

        List<Book> authorBooks = bookRepository.findByAuthorFullName(author.getFullName());

        bookRepository.deleteAll(authorBooks);
        authorRepository.delete(author);
    }
}
