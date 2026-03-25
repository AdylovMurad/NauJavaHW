package ru.murad.NauJava.controller;

import org.springframework.web.bind.annotation.*;
import ru.murad.NauJava.repository.BookRepository;
import ru.murad.NauJava.entity.Book;
import ru.murad.NauJava.exception.ResourceNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping("/filter")
    public List<Book> filterBooks(
            @RequestParam String title,
            @RequestParam Integer start,
            @RequestParam Integer end) {
        return bookRepository.findByTitleContainingAndPublicationYearBetween(title, start, end);
    }

    @GetMapping("/by-author")
    public List<Book> getBooksByAuthor(@RequestParam String name) {
        return bookRepository.findByAuthorFullName(name);
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Книга с ID " + id + " не найдена!"));
    }
}