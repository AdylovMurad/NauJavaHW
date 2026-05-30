package ru.murad.NauJava.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.murad.NauJava.entity.Loan;
import ru.murad.NauJava.entity.LoanStatus;
import ru.murad.NauJava.repository.LoanRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanStatusService {

    private final LoanRepository loanRepository;
    private final NotificationService notificationService;

    public LoanStatusService(LoanRepository loanRepository, NotificationService notificationService) {
        this.loanRepository = loanRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public int refreshOverdue() {
        List<Loan> overdueCandidates = loanRepository.findByStatusAndReturnDeadlineBefore(
                LoanStatus.BORROWED,
                LocalDateTime.now()
        );

        if (overdueCandidates.isEmpty()) {
            return 0;
        }

        for (Loan loan : overdueCandidates) {
            loan.setStatus(LoanStatus.OVERDUE);
        }
        loanRepository.saveAll(overdueCandidates);
        for (Loan loan : overdueCandidates) {
            notificationService.notifyOverdue(loan);
        }
        return overdueCandidates.size();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void refreshOverdueHourly() {
        refreshOverdue();
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void notifyDueSoonDaily() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime soon = now.plusDays(2);
        List<Loan> dueSoon = loanRepository.findByStatusAndReturnDeadlineBetween(
                LoanStatus.BORROWED,
                now,
                soon
        );
        for (Loan loan : dueSoon) {
            notificationService.notifyDueSoon(loan);
        }
    }
}
