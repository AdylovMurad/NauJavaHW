package ru.murad.NauJava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.murad.NauJava.entity.Book;
import ru.murad.NauJava.repository.BookRepository;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void createBook(Long id, String title, String author) {
        Book newBook = new Book(id, title, author);
        bookRepository.create(newBook);
    }

    @Override
    public Book findById(Long id) {
        return bookRepository.read(id);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.delete(id);
    }

    @Override
    public void updateBook(Long id, String newTitle, String newAuthor) {
        Book existingBook = bookRepository.read(id);
        if (existingBook != null) {
            existingBook.setTitle(newTitle);
            existingBook.setAuthor(newAuthor);
            bookRepository.update(existingBook);
        } else {
            System.out.println("Книга с ID " + id + " не найдена.");
        }
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
}