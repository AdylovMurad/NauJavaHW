package ru.murad.NauJava.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.murad.NauJava.entity.Book;
import ru.murad.NauJava.entity.Loan;
import ru.murad.NauJava.entity.LoanStatus;
import ru.murad.NauJava.entity.User;
import ru.murad.NauJava.repository.BookRepository;
import ru.murad.NauJava.repository.LoanRepository;
import ru.murad.NauJava.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class BookViewController {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    public BookViewController(BookRepository bookRepository, LoanRepository loanRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
    }

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
                List.of(LoanStatus.BOOKED, LoanStatus.BORROWED)
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

        model.addAttribute("message", "Книга успешно забронирована");
        return showAllBooks(model, null, null, null);
    }

    @GetMapping("/ui/profile")
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<Loan> loans = loanRepository.findByUserAndStatusIn(
                user,
                List.of(LoanStatus.BOOKED, LoanStatus.BORROWED)
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