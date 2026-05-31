package ru.murad.SmartLibrary.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.murad.SmartLibrary.entity.Author;
import ru.murad.SmartLibrary.entity.Book;
import ru.murad.SmartLibrary.entity.Genre;
import ru.murad.SmartLibrary.entity.Loan;
import ru.murad.SmartLibrary.entity.LoanStatus;
import ru.murad.SmartLibrary.entity.Report;
import ru.murad.SmartLibrary.entity.User;
import ru.murad.SmartLibrary.entity.UserRole;
import ru.murad.SmartLibrary.repository.AuthorRepository;
import ru.murad.SmartLibrary.repository.BookRepository;
import ru.murad.SmartLibrary.repository.GenreRepository;
import ru.murad.SmartLibrary.repository.LoanRepository;
import ru.murad.SmartLibrary.repository.ReportRepository;
import ru.murad.SmartLibrary.repository.UserRepository;
import ru.murad.SmartLibrary.service.AuthorService;
import ru.murad.SmartLibrary.service.LoanStatusService;
import ru.murad.SmartLibrary.service.ReportService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Контроллер для административной панели управления библиотекой.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final AuthorService authorService;
    private final ReportService reportService;
    private final ReportRepository reportRepository;
    private final LoanStatusService loanStatusService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Конструктор контроллера AdminController.
     *
     * @param bookRepository     репозиторий книг
     * @param authorRepository   репозиторий авторов
     * @param genreRepository    репозиторий жанров
     * @param userRepository     репозиторий пользователей
     * @param loanRepository     репозиторий выдач/бронирований
     * @param authorService      сервис управления авторами
     * @param reportService      сервис управления отчетами
     * @param reportRepository   репозиторий отчетов
     * @param loanStatusService  сервис контроля статусов выдачи
     * @param passwordEncoder    кодировщик паролей
     */
    public AdminController(BookRepository bookRepository,
                           AuthorRepository authorRepository,
                           GenreRepository genreRepository,
                           UserRepository userRepository,
                           LoanRepository loanRepository,
                           AuthorService authorService,
                           ReportService reportService,
                           ReportRepository reportRepository,
                           LoanStatusService loanStatusService,
                           PasswordEncoder passwordEncoder) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
        this.authorService = authorService;
        this.reportService = reportService;
        this.reportRepository = reportRepository;
        this.loanStatusService = loanStatusService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Перенаправляет с корня админ-панели на страницу книг.
     *
     * @return перенаправление на страницу книг
     */
    @GetMapping
    public String adminHome() {
        return "redirect:/admin/books";
    }

    /**
     * Отображает страницу управления книгами библиотеки.
     *
     * @param model объект модели Thymeleaf
     * @return путь до шаблона "admin/books"
     */
    @GetMapping("/books")
    public String booksPage(Model model) {
        model.addAttribute("books", bookRepository.findAll());
        model.addAttribute("authors", authorRepository.findAll());
        model.addAttribute("genres", genreRepository.findAll());
        return "admin/books";
    }

    /**
     * Обрабатывает добавление новой книги администратором.
     *
     * @param title           название книги
     * @param isbn            ISBN книги (необязательный параметр)
     * @param description     краткая аннотация (необязательный параметр)
     * @param language        язык издания (необязательный параметр)
     * @param publicationYear год издания (необязательный параметр)
     * @param authorId        идентификатор автора (необязательный параметр)
     * @param genreId         идентификатор жанра (необязательный параметр)
     * @param availableCount  количество экземпляров книги
     * @return перенаправление на страницу книг
     */
    @PostMapping("/books")
    public String createBook(@RequestParam String title,
                             @RequestParam(required = false) String isbn,
                             @RequestParam(required = false) String description,
                             @RequestParam(required = false) String language,
                             @RequestParam(required = false) Integer publicationYear,
                             @RequestParam(required = false) Long authorId,
                             @RequestParam(required = false) Long genreId,
                             @RequestParam(defaultValue = "1") int availableCount) {
        Book book = new Book();
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setDescription(description);
        book.setLanguage(language);
        book.setPublicationYear(publicationYear);
        book.setAvailableCount(availableCount);

        if (authorId != null) {
            authorRepository.findById(authorId).ifPresent(book::setAuthor);
        }
        if (genreId != null) {
            genreRepository.findById(genreId).ifPresent(book::setGenre);
        }

        bookRepository.save(book);
        logger.info("Admin created book id={} title='{}'", book.getId(), book.getTitle());
        return "redirect:/admin/books";
    }

    /**
     * Обновляет информацию о существующей книге.
     *
     * @param id              идентификатор книги
     * @param title           новое название книги
     * @param isbn            новый ISBN (необязательный параметр)
     * @param description     новое описание (необязательный параметр)
     * @param language        новый язык (необязательный параметр)
     * @param publicationYear новый год (необязательный параметр)
     * @param authorId        новый автор (необязательный параметр)
     * @param genreId         новый жанр (необязательный параметр)
     * @param availableCount  новое количество экземпляров
     * @return перенаправление на страницу книг
     */
    @PostMapping("/books/{id}/update")
    public String updateBook(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam(required = false) String isbn,
                             @RequestParam(required = false) String description,
                             @RequestParam(required = false) String language,
                             @RequestParam(required = false) Integer publicationYear,
                             @RequestParam(required = false) Long authorId,
                             @RequestParam(required = false) Long genreId,
                             @RequestParam(defaultValue = "1") int availableCount) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));

        book.setTitle(title);
        book.setIsbn(isbn);
        book.setDescription(description);
        book.setLanguage(language);
        book.setPublicationYear(publicationYear);
        book.setAvailableCount(availableCount);

        if (authorId != null) {
            Author author = authorRepository.findById(authorId).orElse(null);
            book.setAuthor(author);
        } else {
            book.setAuthor(null);
        }

        if (genreId != null) {
            Genre genre = genreRepository.findById(genreId).orElse(null);
            book.setGenre(genre);
        } else {
            book.setGenre(null);
        }

        bookRepository.save(book);
        logger.info("Admin updated book id={} title='{}'", book.getId(), book.getTitle());
        return "redirect:/admin/books";
    }

    /**
     * Удаляет книгу по её идентификатору.
     *
     * @param id идентификатор удаляемой книги
     * @return перенаправление на страницу книг
     */
    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        logger.info("Admin deleted book id={}", id);
        return "redirect:/admin/books";
    }

    /**
     * Отображает страницу управления авторами.
     *
     * @param model объект модели Thymeleaf
     * @return путь до шаблона "admin/authors"
     */
    @GetMapping("/authors")
    public String authorsPage(Model model) {
        model.addAttribute("authors", authorRepository.findAll());
        return "admin/authors";
    }

    /**
     * Создает нового автора.
     *
     * @param fullName    ФИО автора
     * @param biography   биография автора (необязательный параметр)
     * @param birthDate   дата рождения автора (необязательный параметр)
     * @param dateOfDeath дата смерти автора (необязательный параметр)
     * @return перенаправление на страницу авторов
     */
    @PostMapping("/authors")
    public String createAuthor(@RequestParam String fullName,
                               @RequestParam(required = false) String biography,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfDeath) {
        Author author = new Author();
        author.setFullName(fullName);
        author.setBiography(biography);
        author.setBirthDate(birthDate);
        author.setDateOfDeath(dateOfDeath);
        authorRepository.save(author);
        logger.info("Admin created author id={} name='{}'", author.getId(), author.getFullName());
        return "redirect:/admin/authors";
    }

    /**
     * Удаляет автора и все связанные с ним книги из базы данных.
     *
     * @param id идентификатор автора
     * @return перенаправление на страницу авторов
     */
    @PostMapping("/authors/{id}/delete")
    public String deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthorWithBooks(id);
        logger.info("Admin deleted author id={}", id);
        return "redirect:/admin/authors";
    }

    /**
     * Отображает страницу управления жанрами.
     *
     * @param model объект модели Thymeleaf
     * @return путь до шаблона "admin/genres"
     */
    /**
     * Отображает страницу управления жанрами библиотеки.
     *
     * @param model объект модели Thymeleaf
     * @return путь до шаблона "admin/genres"
     */
    @GetMapping("/genres")
    public String genresPage(Model model) {
        List<Genre> genres = genreRepository.findAll();
        double avgPopularity = genres.isEmpty() ? 0.0 : genres.stream()
                .mapToDouble(g -> g.getPopularityIndex() != null ? g.getPopularityIndex() : 0)
                .average()
                .orElse(0.0);
        model.addAttribute("genres", genres);
        model.addAttribute("avgPopularity", avgPopularity);
        return "admin/genres";
    }

    /**
     * Создает новый литературный жанр.
     *
     * @param name            название жанра
     * @param description     описание жанра (необязательный параметр)
     * @param popularityIndex индекс популярности (необязательный параметр)
     * @return перенаправление на страницу жанров
     */
    @PostMapping("/genres")
    public String createGenre(@RequestParam String name,
                              @RequestParam(required = false) String description,
                              @RequestParam(required = false) Integer popularityIndex) {
        Genre genre = new Genre();
        genre.setName(name);
        genre.setDescription(description);
        genre.setPopularityIndex(popularityIndex);
        genreRepository.save(genre);
        logger.info("Admin created genre id={} name='{}'", genre.getId(), genre.getName());
        return "redirect:/admin/genres";
    }

    /**
     * Удаляет жанр по его идентификатору.
     *
     * @param id идентификатор жанра
     * @return перенаправление на страницу жанров
     */
    @PostMapping("/genres/{id}/delete")
    public String deleteGenre(@PathVariable Long id) {
        genreRepository.deleteById(id);
        logger.info("Admin deleted genre id={}", id);
        return "redirect:/admin/genres";
    }

    /**
     * Отображает страницу со списком всех зарегистрированных читателей.
     *
     * @param model объект модели Thymeleaf
     * @return путь до шаблона "admin/users"
     */
    @GetMapping("/users")
    public String usersPage(Model model) {
        Iterable<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

    /**
     * Создает нового пользователя (администратор).
     *
     * @param username логин пользователя
     * @param password пароль пользователя
     * @param email email пользователя
     * @param firstName имя пользователя (необязательно)
     * @param lastName фамилия пользователя (необязательно)
     * @param role роль пользователя (USER или ADMIN)
     * @return перенаправление на страницу пользователей
     */
    @PostMapping("/users")
    public String createUser(@RequestParam String username,
                            @RequestParam String password,
                            @RequestParam String email,
                            @RequestParam(required = false) String firstName,
                            @RequestParam(required = false) String lastName,
                            @RequestParam String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(UserRole.valueOf("ROLE_" + role));
        userRepository.save(user);
        logger.info("Admin created user id={} username='{}'", user.getId(), user.getUsername());
        return "redirect:/admin/users";
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return перенаправление на страницу пользователей
     */
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        logger.info("Admin deleted user id={}", id);
        return "redirect:/admin/users";
    }

    /**
     * Обновляет информацию о пользователе (администратор).
     *
     * @param id идентификатор пользователя
     * @param password новый пароль (необязательно)
     * @param email новый email
     * @param firstName новое имя
     * @param lastName новая фамилия
     * @param role новая роль
     * @return перенаправление на страницу пользователей
     */
    @PostMapping("/users/{id}/update")
    public String updateUser(@PathVariable Long id,
                            @RequestParam(required = false) String password,
                            @RequestParam String email,
                            @RequestParam(required = false) String firstName,
                            @RequestParam(required = false) String lastName,
                            @RequestParam String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(UserRole.valueOf("ROLE_" + role));
        
        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        
        userRepository.save(user);
        logger.info("Admin updated user id={} username='{}'", user.getId(), user.getUsername());
        return "redirect:/admin/users";
    }

    /**
     * Отображает страницу контроля выдач и бронирований.
     *
     * @param model объект модели Thymeleaf
     * @return путь до шаблона "admin/loans"
     */
    @GetMapping("/loans")
    public String loansPage(Model model) {
        loanStatusService.refreshOverdue();
        List<Loan> booked = loanRepository.findByStatus(LoanStatus.BOOKED);
        List<Loan> borrowed = loanRepository.findByStatus(LoanStatus.BORROWED);
        List<Loan> overdue = loanRepository.findByStatus(LoanStatus.OVERDUE);
        model.addAttribute("booked", booked);
        model.addAttribute("borrowed", borrowed);
        model.addAttribute("overdue", overdue);
        return "admin/loans";
    }

    /**
     * Обрабатывает выдачу забронированной книги читателю.
     *
     * @param loanId идентификатор записи бронирования
     * @return перенаправление на страницу выдач
     */
    @PostMapping("/loans/issue")
    public String issueLoan(@RequestParam Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Бронь не найдена"));

        if (loan.getStatus() == LoanStatus.BOOKED) {
            loan.setStatus(LoanStatus.BORROWED);
            loan.setLoanDate(LocalDateTime.now());
            loanRepository.save(loan);
            logger.info("Admin issued loan id={} bookId={} userId={}", loan.getId(), loan.getBook().getId(), loan.getUser().getId());
        }

        return "redirect:/admin/loans";
    }

    /**
     * Обрабатывает возврат выданной или просроченной книги обратно в библиотеку.
     *
     * @param loanId идентификатор записи выдачи
     * @return перенаправление на страницу выдач
     */
    @PostMapping("/loans/return")
    public String returnLoan(@RequestParam Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Выдача не найдена"));

        if (loan.getStatus() == LoanStatus.BORROWED || loan.getStatus() == LoanStatus.OVERDUE) {
            loan.setStatus(LoanStatus.RETURNED);
            loan.setLoanDate(LocalDateTime.now());
            loanRepository.save(loan);

            Book book = loan.getBook();
            if (book != null) {
                book.setAvailableCount(book.getAvailableCount() + 1);
                bookRepository.save(book);
            }
            logger.info("Admin returned loan id={} bookId={} userId={}", loan.getId(), loan.getBook().getId(), loan.getUser().getId());
        }

        return "redirect:/admin/loans";
    }

    /**
     * Отображает страницу общей статистики библиотеки.
     *
     * @param model объект модели Thymeleaf
     * @return путь до шаблона "admin/stats"
     */
    @GetMapping("/stats")
    public String statsPage(Model model) {
        addStats(model);
        return "admin/stats";
    }

    /**
     * Обрабатывает запрос на генерацию асинхронного HTML-отчета.
     *
     * @param model объект модели Thymeleaf
     * @return путь до шаблона "admin/stats"
     */
    @PostMapping("/report/generate")
    public String generateReport(Model model) {
        Long id = reportService.createReport();
        reportService.generateReportAsync(id);
        Report report = reportRepository.findById(id).orElse(null);

        model.addAttribute("reportId", id);
        model.addAttribute("reportStatus", report != null ? report.getStatus() : null);
        addStats(model);
        logger.info("Admin generated report id={}", id);
        return "admin/stats";
    }

    private void addStats(Model model) {
        loanStatusService.refreshOverdue();
        long totalBooks = bookRepository.count();
        long totalUsers = userRepository.count();
        long booked = loanRepository.countByStatus(LoanStatus.BOOKED);
        long borrowed = loanRepository.countByStatus(LoanStatus.BORROWED);
        long overdue = loanRepository.countByStatus(LoanStatus.OVERDUE);
        long returned = loanRepository.countByStatus(LoanStatus.RETURNED);

        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("booked", booked);
        model.addAttribute("borrowed", borrowed);
        model.addAttribute("overdue", overdue);
        model.addAttribute("returned", returned);
    }
}
