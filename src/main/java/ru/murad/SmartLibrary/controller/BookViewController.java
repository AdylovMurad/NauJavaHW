package ru.murad.SmartLibrary.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.murad.SmartLibrary.entity.Book;
import ru.murad.SmartLibrary.entity.Loan;
import ru.murad.SmartLibrary.entity.LoanStatus;
import ru.murad.SmartLibrary.entity.User;
import ru.murad.SmartLibrary.repository.BookRepository;
import ru.murad.SmartLibrary.repository.LoanRepository;
import ru.murad.SmartLibrary.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Контроллер для отображения страниц пользовательского интерфейса (UI) каталога книг и личного кабинета.
 */
@Controller
public class BookViewController {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    /**
     * Конструктор контроллера BookViewController.
     *
     * @param bookRepository  репозиторий книг
     * @param loanRepository  репозиторий выдач/бронирований
     * @param userRepository  репозиторий пользователей
     */
    public BookViewController(BookRepository bookRepository, LoanRepository loanRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
    }

    /**
     * Отображает страницу со списком книг с возможностью фильтрации по названию, автору и жанру.
     *
     * @param model  объект модели Thymeleaf для передачи данных на HTML-страницу
     * @param title  название книги для фильтрации (необязательный параметр)
     * @param author имя автора для фильтрации (необязательный параметр)
     * @param genre  название жанра для фильтрации (необязательный параметр)
     * @return имя Thymeleaf-шаблона "books"
     */
    @GetMapping("/ui/books")
    public String showAllBooks(Model model,
                               @RequestParam(required = false) String title,
                               @RequestParam(required = false) String author,
                               @RequestParam(required = false) String genre) {
        String titleFilter = normalize(title);
        String authorFilter = normalize(author);
        String genreFilter = normalize(genre);

        List<Book> books = bookRepository.searchBooks(titleFilter, authorFilter, genreFilter);
        model.addAttribute("books", books);
        model.addAttribute("title", title);
        model.addAttribute("author", author);
        model.addAttribute("genre", genre);
        return "books";
    }

    /**
     * Обрабатывает POST-запрос на бронирование выбранной книги из каталога.
     *
     * @param id          идентификатор бронируемой книги
     * @param userDetails данные текущего авторизованного пользователя
     * @param model       объект модели Thymeleaf
     * @return перенаправление на страницу каталога книг
     */
    @PostMapping("/ui/books/{id}/book")
    public String bookFromCatalog(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        if (userDetails == null) {
            model.addAttribute("error", "Нужно войти в систему, чтобы забронировать книгу");
            return showAllBooks(model, null, null, null);
        }

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));

        if (book.getAvailableCount() <= 0) {
            model.addAttribute("error", "Нет доступных экземпляров для бронирования");
            return showAllBooks(model, null, null, null);
        }

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        boolean alreadyHasBook = loanRepository.existsByBookIdAndUserIdAndStatusIn(
                id,
                user.getId(),
            List.of(LoanStatus.BOOKED, LoanStatus.BORROWED, LoanStatus.OVERDUE)
        );

        if (alreadyHasBook) {
            model.addAttribute("error", "Вы уже забронировали или взяли эту книгу");
            return showAllBooks(model, null, null, null);
        }

        book.setAvailableCount(book.getAvailableCount() - 1);
        bookRepository.save(book);

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setStatus(LoanStatus.BOOKED);
        loan.setLoanDate(LocalDateTime.now());
        loan.setReturnDeadline(LocalDateTime.now().plusDays(14));
        loanRepository.save(loan);

        return "redirect:/ui/profile";
    }

    /**
     * Отображает страницу личного кабинета пользователя со списком его активных броней и выдач.
     *
     * @param userDetails данные авторизованного пользователя
     * @param model       объект модели Thymeleaf
     * @return имя Thymeleaf-шаблона "profile" или перенаправление на "/login"
     */
    @GetMapping("/ui/profile")
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<Loan> loans = loanRepository.findByUserAndStatusIn(
                user,
            List.of(LoanStatus.BOOKED, LoanStatus.BORROWED, LoanStatus.OVERDUE)
        );

        model.addAttribute("loans", loans);
        model.addAttribute("username", user.getUsername());
        return "profile";
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
