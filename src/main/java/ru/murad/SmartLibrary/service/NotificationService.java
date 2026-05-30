package ru.murad.SmartLibrary.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.murad.SmartLibrary.entity.Loan;

/**
 * Сервис асинхронной имитации отправки уведомлений читателям.
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    /**
     * Асинхронно логирует/имитирует отправку уведомления о скором наступлении дедлайна возврата книги.
     *
     * @param loan запись выдачи, срок которой истекает
     */
    @Async
    public void notifyDueSoon(Loan loan) {
        logger.info("Notify due soon: loanId={} userId={} bookId={} deadline={}",
                loan.getId(),
                loan.getUser().getId(),
                loan.getBook().getId(),
                loan.getReturnDeadline());
    }

    /**
     * Асинхронно логирует/имитирует отправку уведомления о просроченном возврате книги.
     *
     * @param loan запись просроченной выдачи
     */
    @Async
    public void notifyOverdue(Loan loan) {
        logger.info("Notify overdue: loanId={} userId={} bookId={} deadline={}",
                loan.getId(),
                loan.getUser().getId(),
                loan.getBook().getId(),
                loan.getReturnDeadline());
    }
}
