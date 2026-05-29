package ru.murad.NauJava.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.murad.NauJava.entity.Book;
import ru.murad.NauJava.entity.Loan;
import ru.murad.NauJava.entity.LoanStatus;
import ru.murad.NauJava.entity.User;
import ru.murad.NauJava.repository.BookRepository;
import ru.murad.NauJava.repository.LoanRepository;
import ru.murad.NauJava.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoanRestController {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    public LoanRestController(BookRepository bookRepository, LoanRepository loanRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/book/{bookId}")
    public ResponseEntity<String> bookBook(@PathVariable Long bookId, @AuthenticationPrincipal UserDetails userDetails) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));

        if (book.getAvailableCount() <= 0) {
            return ResponseEntity.badRequest().body("Нет доступных экземпляров для бронирования!");
        }

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        boolean alreadyHasBook = loanRepository.existsByBookIdAndUserIdAndStatusIn(
                bookId,
                user.getId(),
                List.of(LoanStatus.BOOKED, LoanStatus.BORROWED)
        );

        if (alreadyHasBook) {
            return ResponseEntity.badRequest().body("Вы уже забронировали или взяли эту книгу! Нельзя взять два одинаковых экземпляра.");
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

        return ResponseEntity.ok("Книга '" + book.getTitle() + "' успешно забронирована!");
    }

    @GetMapping("/my")
    public ResponseEntity<List<Loan>> getMyLoans(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return ResponseEntity.ok(loanRepository.findByUser(user));
    }

    @PostMapping("/admin/issue")
    public ResponseEntity<String> issueBook(@RequestParam Long bookId, @RequestParam Long userId) {
        Loan loan = loanRepository.findByBookIdAndUserIdAndStatus(bookId, userId, LoanStatus.BOOKED)
                .orElseThrow(() -> new RuntimeException("Активная бронь для данного пользователя и книги не найдена"));

        loan.setStatus(LoanStatus.BORROWED);
        loan.setLoanDate(LocalDateTime.now());
        loanRepository.save(loan);

        return ResponseEntity.ok("Книга успешно выдана читателю!");
    }

    @PostMapping("/admin/return")
    public ResponseEntity<String> returnBook(@RequestParam Long bookId, @RequestParam Long userId) {
        Loan loan = loanRepository.findByBookIdAndUserIdAndStatus(bookId, userId, LoanStatus.BORROWED)
            .or(() -> loanRepository.findByBookIdAndUserIdAndStatus(bookId, userId, LoanStatus.OVERDUE))
            .orElseThrow(() -> new RuntimeException("Запись о выдаче этой книги данному пользователю не найдена"));

        loan.setStatus(LoanStatus.RETURNED);
        loanRepository.save(loan);

        Book book = loan.getBook();
        book.setAvailableCount(book.getAvailableCount() + 1);
        bookRepository.save(book);

        return ResponseEntity.ok("Книга успешно возвращена в библиотеку. Баланс обновлен!");
    }

    @GetMapping("/admin/statistics")
    public ResponseEntity<Map<String, Long>> getStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.clear();
        stats.put("total_booked", loanRepository.countByStatus(LoanStatus.BOOKED));
        stats.put("total_borrowed", loanRepository.countByStatus(LoanStatus.BORROWED));
        stats.put("total_overdue", loanRepository.countByStatus(LoanStatus.OVERDUE));
        stats.put("total_returned", loanRepository.countByStatus(LoanStatus.RETURNED));

        return ResponseEntity.ok(stats);
    }
}