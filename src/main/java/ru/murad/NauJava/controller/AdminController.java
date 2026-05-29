package ru.murad.NauJava.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.murad.NauJava.entity.Author;
import ru.murad.NauJava.entity.Book;
import ru.murad.NauJava.entity.Genre;
import ru.murad.NauJava.entity.Loan;
import ru.murad.NauJava.entity.LoanStatus;
import ru.murad.NauJava.entity.Report;
import ru.murad.NauJava.entity.User;
import ru.murad.NauJava.repository.AuthorRepository;
import ru.murad.NauJava.repository.BookRepository;
import ru.murad.NauJava.repository.GenreRepository;
import ru.murad.NauJava.repository.LoanRepository;
import ru.murad.NauJava.repository.ReportRepository;
import ru.murad.NauJava.repository.UserRepository;
import ru.murad.NauJava.service.AuthorService;
import ru.murad.NauJava.service.ReportService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final AuthorService authorService;
    private final ReportService reportService;
    private final ReportRepository reportRepository;

    public AdminController(BookRepository bookRepository,
                           AuthorRepository authorRepository,
                           GenreRepository genreRepository,
                           UserRepository userRepository,
                           LoanRepository loanRepository,
                           AuthorService authorService,
                           ReportService reportService,
                           ReportRepository reportRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
        this.authorService = authorService;
        this.reportService = reportService;
        this.reportRepository = reportRepository;
    }

    @GetMapping
    public String adminHome() {
        return "redirect:/admin/books";
    }

    @GetMapping("/books")
    public String booksPage(Model model) {
        model.addAttribute("books", bookRepository.findAll());
        model.addAttribute("authors", authorRepository.findAll());
        model.addAttribute("genres", genreRepository.findAll());
        return "admin/books";
    }

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
        return "redirect:/admin/books";
    }

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
        return "redirect:/admin/books";
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return "redirect:/admin/books";
    }

    @GetMapping("/authors")
    public String authorsPage(Model model) {
        model.addAttribute("authors", authorRepository.findAll());
        return "admin/authors";
    }

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
        return "redirect:/admin/authors";
    }

    @PostMapping("/authors/{id}/delete")
    public String deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthorWithBooks(id);
        return "redirect:/admin/authors";
    }

    @GetMapping("/genres")
    public String genresPage(Model model) {
        model.addAttribute("genres", genreRepository.findAll());
        return "admin/genres";
    }

    @PostMapping("/genres")
    public String createGenre(@RequestParam String name,
                              @RequestParam(required = false) String description,
                              @RequestParam(required = false) Integer popularityIndex) {
        Genre genre = new Genre();
        genre.setName(name);
        genre.setDescription(description);
        genre.setPopularityIndex(popularityIndex);
        genreRepository.save(genre);
        return "redirect:/admin/genres";
    }

    @PostMapping("/genres/{id}/delete")
    public String deleteGenre(@PathVariable Long id) {
        genreRepository.deleteById(id);
        return "redirect:/admin/genres";
    }

    @GetMapping("/users")
    public String usersPage(Model model) {
        Iterable<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/loans")
    public String loansPage(Model model) {
        List<Loan> booked = loanRepository.findByStatus(LoanStatus.BOOKED);
        List<Loan> borrowed = loanRepository.findByStatus(LoanStatus.BORROWED);
        model.addAttribute("booked", booked);
        model.addAttribute("borrowed", borrowed);
        return "admin/loans";
    }

    @PostMapping("/loans/issue")
    public String issueLoan(@RequestParam Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Бронь не найдена"));

        if (loan.getStatus() == LoanStatus.BOOKED) {
            loan.setStatus(LoanStatus.BORROWED);
            loan.setLoanDate(LocalDateTime.now());
            loanRepository.save(loan);
        }

        return "redirect:/admin/loans";
    }

    @PostMapping("/loans/return")
    public String returnLoan(@RequestParam Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Выдача не найдена"));

        if (loan.getStatus() == LoanStatus.BORROWED) {
            loan.setStatus(LoanStatus.RETURNED);
            loanRepository.save(loan);

            Book book = loan.getBook();
            if (book != null) {
                book.setAvailableCount(book.getAvailableCount() + 1);
                bookRepository.save(book);
            }
        }

        return "redirect:/admin/loans";
    }

    @GetMapping("/stats")
    public String statsPage(Model model) {
        addStats(model);
        return "admin/stats";
    }

    @PostMapping("/report/generate")
    public String generateReport(Model model) {
        Long id = reportService.createReport();
        reportService.generateReportAsync(id);
        Report report = reportRepository.findById(id).orElse(null);

        model.addAttribute("reportId", id);
        model.addAttribute("reportStatus", report != null ? report.getStatus() : null);
        addStats(model);
        return "admin/stats";
    }

    private void addStats(Model model) {
        long totalBooks = bookRepository.count();
        long totalUsers = userRepository.count();
        long booked = loanRepository.countByStatus(LoanStatus.BOOKED);
        long borrowed = loanRepository.countByStatus(LoanStatus.BORROWED);
        long returned = loanRepository.countByStatus(LoanStatus.RETURNED);

        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("booked", booked);
        model.addAttribute("borrowed", borrowed);
        model.addAttribute("returned", returned);
    }
}
