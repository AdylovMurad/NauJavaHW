package ru.murad.NauJava.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.murad.NauJava.entity.Book;
import ru.murad.NauJava.exception.ResourceNotFoundException;
import ru.murad.NauJava.repository.BookRepository;
import ru.murad.NauJava.service.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;
    private final BookService bookService;

    public BookController(BookRepository bookRepository, BookService bookService) {
        this.bookRepository = bookRepository;
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Книга с ID " + id + " не найдена!"));
        return ResponseEntity.ok(book);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Book>> filterBooks(
            @RequestParam String title,
            @RequestParam Integer start,
            @RequestParam Integer end) {
        List<Book> books = bookRepository.findByTitleContainingAndPublicationYearBetween(title, start, end);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/by-author")
    public ResponseEntity<List<Book>> getBooksByAuthor(@RequestParam String name) {
        List<Book> books = bookRepository.findByAuthorFullName(name);
        return ResponseEntity.ok(books);
    }

    @PostMapping("/admin")
    public ResponseEntity<String> createBook(
            @RequestParam String title,
            @RequestParam String authorName,
            @RequestParam int availableCount) {

        bookService.createBook(null, title, authorName);
        return ResponseEntity.ok("Книга успешно добавлена!");
    }
}