package ru.murad.SmartLibrary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.murad.SmartLibrary.entity.Book;
import ru.murad.SmartLibrary.entity.Loan;
import ru.murad.SmartLibrary.entity.LoanStatus;
import ru.murad.SmartLibrary.entity.User;
import ru.murad.SmartLibrary.repository.LoanRepository;
import ru.murad.SmartLibrary.service.LoanStatusService;
import ru.murad.SmartLibrary.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class LoanStatusServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LoanStatusService loanStatusService;

    @Test
    public void refreshOverdue_NoCandidates_ReturnsZero() {
        Mockito.when(loanRepository.findByStatusAndReturnDeadlineBefore(Mockito.eq(LoanStatus.BORROWED), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of());

        int updated = loanStatusService.refreshOverdue();

        Assertions.assertEquals(0, updated);
        Mockito.verify(loanRepository, Mockito.never()).saveAll(Mockito.anyList());
        Mockito.verify(notificationService, Mockito.never()).notifyOverdue(Mockito.any());
    }

    @Test
    public void refreshOverdue_UpdatesStatusesAndNotifies() {
        Loan loan1 = createLoan(1L);
        Loan loan2 = createLoan(2L);
        Mockito.when(loanRepository.findByStatusAndReturnDeadlineBefore(Mockito.eq(LoanStatus.BORROWED), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(loan1, loan2));

        int updated = loanStatusService.refreshOverdue();

        Assertions.assertEquals(2, updated);
        Assertions.assertEquals(LoanStatus.OVERDUE, loan1.getStatus());
        Assertions.assertEquals(LoanStatus.OVERDUE, loan2.getStatus());
        Mockito.verify(loanRepository).saveAll(Mockito.anyList());
        Mockito.verify(notificationService).notifyOverdue(loan1);
        Mockito.verify(notificationService).notifyOverdue(loan2);
    }

    @Test
    public void notifyDueSoonDaily_SendsNotifications() {
        Loan loan = createLoan(3L);
        Mockito.when(loanRepository.findByStatusAndReturnDeadlineBetween(
                Mockito.eq(LoanStatus.BORROWED),
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(loan));

        loanStatusService.notifyDueSoonDaily();

        Mockito.verify(notificationService).notifyDueSoon(loan);
    }

    private Loan createLoan(Long id) {
        Loan loan = new Loan();
        loan.setId(id);
        loan.setStatus(LoanStatus.BORROWED);
        loan.setReturnDeadline(LocalDateTime.now().minusDays(1));
        Book book = new Book();
        book.setId(10L + id);
        User user = new User();
        user.setId(20L + id);
        loan.setBook(book);
        loan.setUser(user);
        return loan;
    }
}
