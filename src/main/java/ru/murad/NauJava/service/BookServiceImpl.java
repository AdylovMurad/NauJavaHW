package ru.murad.NauJava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.murad.NauJava.entity.Book;
import ru.murad.NauJava.entity.Author;
import ru.murad.NauJava.repository.BookRepository;
import ru.murad.NauJava.repository.AuthorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @Override
    public void createBook(Long id, String title, String authorName) {
        Book book = new Book();
        book.setTitle(title);

        Author author = new Author();
        author.setFullName(authorName);
        authorRepository.save(author);

        book.setAuthor(author);
        bookRepository.save(book);
    }

    @Override
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        bookRepository.findAll().forEach(books::add);
        return books;
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public void updateBook(Long id, String title, String authorName) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Книга с ID " + id + " не найдена."));

        book.setTitle(title);

        if (book.getAuthor() != null) {
            book.getAuthor().setFullName(authorName);
            authorRepository.save(book.getAuthor());
        }

        bookRepository.save(book);
    }

    @Override
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }
}