package ru.murad.NauJava.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.murad.NauJava.entity.Loan;
import ru.murad.NauJava.entity.LoanStatus;
import ru.murad.NauJava.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends CrudRepository<Loan, Long> {
    List<Loan> findByUser(User user);

    Optional<Loan> findByBookIdAndUserIdAndStatus(Long bookId, Long userId, LoanStatus status);

    long countByStatus(LoanStatus status);
    boolean existsByBookIdAndUserIdAndStatusIn(Long bookId, Long userId, List<LoanStatus> statuses);
}