package ru.murad.NauJava.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.murad.NauJava.entity.Loan;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Async
    public void notifyDueSoon(Loan loan) {
        logger.info("Notify due soon: loanId={} userId={} bookId={} deadline={}",
                loan.getId(),
                loan.getUser().getId(),
                loan.getBook().getId(),
                loan.getReturnDeadline());
    }

    @Async
    public void notifyOverdue(Loan loan) {
        logger.info("Notify overdue: loanId={} userId={} bookId={} deadline={}",
                loan.getId(),
                loan.getUser().getId(),
                loan.getBook().getId(),
                loan.getReturnDeadline());
    }
}
