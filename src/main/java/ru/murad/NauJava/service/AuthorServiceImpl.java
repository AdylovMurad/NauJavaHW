package ru.murad.NauJava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.murad.NauJava.entity.Author;
import ru.murad.NauJava.entity.Book;
import ru.murad.NauJava.repository.AuthorRepository;
import ru.murad.NauJava.repository.BookRepository;
import ru.murad.NauJava.exception.ResourceNotFoundException;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

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