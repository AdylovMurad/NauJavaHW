package ru.murad.NauJava.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.murad.NauJava.controller.dto.BookRequest;
import ru.murad.NauJava.entity.Book;
import ru.murad.NauJava.entity.Author;
import ru.murad.NauJava.entity.Genre;
import ru.murad.NauJava.exception.ResourceNotFoundException;
import ru.murad.NauJava.repository.AuthorRepository;
import ru.murad.NauJava.repository.BookRepository;
import ru.murad.NauJava.repository.GenreRepository;
import ru.murad.NauJava.service.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;
    private final BookService bookService;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    public BookController(BookRepository bookRepository,
                          BookService bookService,
                          AuthorRepository authorRepository,
                          GenreRepository genreRepository) {
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
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

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre) {
        List<Book> books = bookRepository.searchBooks(normalize(title), normalize(author), normalize(genre));
        return ResponseEntity.ok(books);
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody BookRequest request) {
        Book book = new Book();
        applyBookRequest(book, request);
        return ResponseEntity.ok(bookRepository.save(book));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Книга не найдена"));
        applyBookRequest(book, request);
        return ResponseEntity.ok(bookRepository.save(book));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Книга не найдена");
        }
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin")
    public ResponseEntity<String> createBookAdmin(
            @RequestParam String title,
            @RequestParam String authorName,
            @RequestParam int availableCount) {

        bookService.createBook(null, title, authorName);
        return ResponseEntity.ok("Книга успешно добавлена!");
    }

    private void applyBookRequest(Book book, BookRequest request) {
        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setDescription(request.getDescription());
        book.setLanguage(request.getLanguage());
        book.setPublicationYear(request.getPublicationYear());
        if (request.getAvailableCount() != null) {
            book.setAvailableCount(request.getAvailableCount());
        }

        Author author = null;
        if (request.getAuthorId() != null) {
            author = authorRepository.findById(request.getAuthorId()).orElse(null);
        }
        book.setAuthor(author);

        Genre genre = null;
        if (request.getGenreId() != null) {
            genre = genreRepository.findById(request.getGenreId()).orElse(null);
        }
        book.setGenre(genre);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}