package ru.murad.SmartLibrary.repository;

import org.springframework.stereotype.Repository;
import ru.murad.SmartLibrary.entity.Loan;
import ru.murad.SmartLibrary.entity.LoanStatus;
import ru.murad.SmartLibrary.entity.User;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface LoanRepository extends org.springframework.data.repository.CrudRepository<Loan, Long> {
    List<Loan> findByUser(User user);

    List<Loan> findByUserAndStatusIn(User user, List<LoanStatus> statuses);

    List<Loan> findByStatus(LoanStatus status);

    List<Loan> findByStatusAndReturnDeadlineBefore(LoanStatus status, LocalDateTime deadline);

    List<Loan> findByStatusAndReturnDeadlineBetween(LoanStatus status, LocalDateTime start, LocalDateTime end);

    Optional<Loan> findByBookIdAndUserIdAndStatus(Long bookId, Long userId, LoanStatus status);

    long countByStatus(LoanStatus status);
    boolean existsByBookIdAndUserIdAndStatusIn(Long bookId, Long userId, List<LoanStatus> statuses);
}