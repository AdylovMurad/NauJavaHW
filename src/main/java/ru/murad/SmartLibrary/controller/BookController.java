package ru.murad.SmartLibrary.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.murad.SmartLibrary.controller.dto.BookRequest;
import ru.murad.SmartLibrary.entity.Book;
import ru.murad.SmartLibrary.entity.Author;
import ru.murad.SmartLibrary.entity.Genre;
import ru.murad.SmartLibrary.exception.ResourceNotFoundException;
import ru.murad.SmartLibrary.repository.AuthorRepository;
import ru.murad.SmartLibrary.repository.BookRepository;
import ru.murad.SmartLibrary.repository.GenreRepository;
import ru.murad.SmartLibrary.service.BookService;

import java.util.List;

/**
 * REST-контроллер для управления книгами через API.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    private final BookRepository bookRepository;
    private final BookService bookService;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param bookRepository   репозиторий книг
     * @param bookService      сервис управления книгами
     * @param authorRepository репозиторий авторов
     * @param genreRepository  репозиторий жанров
     */
    public BookController(BookRepository bookRepository,
                          BookService bookService,
                          AuthorRepository authorRepository,
                          GenreRepository genreRepository) {
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
    }

    /**
     * Возвращает список всех книг библиотеки.
     *
     * @return ResponseEntity со списком книг
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    /**
     * Находит книгу по её идентификатору.
     *
     * @param id уникальный идентификатор книги
     * @return ResponseEntity с найденной книгой
     * @throws ResourceNotFoundException если книга не найдена
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Книга с ID " + id + " не найдена!"));
        return ResponseEntity.ok(book);
    }

    /**
     * Фильтрует книги по части названия и диапазону лет публикации.
     *
     * @param title поисковый фрагмент названия книги
     * @param start начальный год публикации
     * @param end   конечный год публикации
     * @return ResponseEntity со списком подходящих книг
     */
    @GetMapping("/filter")
    public ResponseEntity<List<Book>> filterBooks(
            @RequestParam String title,
            @RequestParam Integer start,
            @RequestParam Integer end) {
        List<Book> books = bookRepository.findByTitleContainingAndPublicationYearBetween(title, start, end);
        return ResponseEntity.ok(books);
    }

    /**
     * Находит книги по имени автора.
     *
     * @param name полное имя автора
     * @return ResponseEntity со списком книг автора
     */
    @GetMapping("/by-author")
    public ResponseEntity<List<Book>> getBooksByAuthor(@RequestParam String name) {
        List<Book> books = bookRepository.findByAuthorFullName(name);
        return ResponseEntity.ok(books);
    }

    /**
     * Осуществляет поиск книг по названию, автору и жанру.
     *
     * @param title  название книги
     * @param author имя автора
     * @param genre  название жанра
     * @return ResponseEntity со списком книг
     */
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre) {
        List<Book> books = bookRepository.searchBooks(normalize(title), normalize(author), normalize(genre));
        return ResponseEntity.ok(books);
    }

    /**
     * Создает новую книгу на основе REST-запроса.
     *
     * @param request DTO с параметрами книги
     * @return ResponseEntity со свежесозданной книгой
     */
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody BookRequest request) {
        Book book = new Book();
        applyBookRequest(book, request);
        Book saved = bookRepository.save(book);
        logger.info("REST created book id={} title='{}'", saved.getId(), saved.getTitle());
        return ResponseEntity.ok(saved);
    }

    /**
     * Обновляет параметры книги по идентификатору.
     *
     * @param id      идентификатор книги
     * @param request DTO с обновленными параметрами книги
     * @return ResponseEntity с обновленной книгой
     */
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Книга не найдена"));
        applyBookRequest(book, request);
        Book saved = bookRepository.save(book);
        logger.info("REST updated book id={} title='{}'", saved.getId(), saved.getTitle());
        return ResponseEntity.ok(saved);
    }

    /**
     * Удаляет книгу по идентификатору.
     *
     * @param id идентификатор книги
     * @return ResponseEntity без тела (no content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Книга не найдена");
        }
        bookRepository.deleteById(id);
        logger.info("REST deleted book id={}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Административный эндпоинт для быстрого создания книги.
     *
     * @param title          название книги
     * @param authorName     имя автора
     * @param availableCount количество экземпляров
     * @return ResponseEntity с текстовым подтверждением
     */
    @PostMapping("/admin")
    public ResponseEntity<String> createBookAdmin(
                    @RequestParam String title,
                    @RequestParam String authorName,
                    @RequestParam int availableCount) {

        bookService.createBook(null, title, authorName);
        logger.info("REST admin created book title='{}'", title);
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
