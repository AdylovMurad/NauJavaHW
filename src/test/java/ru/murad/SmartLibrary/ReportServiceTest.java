package ru.murad.SmartLibrary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.murad.SmartLibrary.entity.Report;
import ru.murad.SmartLibrary.entity.ReportStatus;
import ru.murad.SmartLibrary.repository.BookRepository;
import ru.murad.SmartLibrary.repository.ReportRepository;
import ru.murad.SmartLibrary.repository.UserRepository;
import ru.murad.SmartLibrary.service.ReportService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    public void createReport_SavesCreatedStatus() {
        Report saved = new Report(ReportStatus.CREATED);
        saved.setId(5L);
        Mockito.when(reportRepository.save(Mockito.any(Report.class))).thenReturn(saved);

        Long id = reportService.createReport();

        Assertions.assertEquals(5L, id);
        Mockito.verify(reportRepository).save(Mockito.any(Report.class));
    }

    @Test
    public void getReportContent_CreatedStatus() {
        Report report = new Report(ReportStatus.CREATED);
        Mockito.when(reportRepository.findById(1L)).thenReturn(Optional.of(report));

        String content = reportService.getReportContent(1L);

        Assertions.assertTrue(content.contains("Отчет еще формируется"));
    }

    @Test
    public void getReportContent_ErrorStatus() {
        Report report = new Report(ReportStatus.ERROR);
        Mockito.when(reportRepository.findById(2L)).thenReturn(Optional.of(report));

        String content = reportService.getReportContent(2L);

        Assertions.assertTrue(content.contains("Ошибка при формировании отчета"));
    }

    @Test
    public void getReportContent_CompletedStatus_ReturnsContent() {
        Report report = new Report(ReportStatus.COMPLETED);
        report.setContent("<html>done</html>");
        Mockito.when(reportRepository.findById(3L)).thenReturn(Optional.of(report));

        String content = reportService.getReportContent(3L);

        Assertions.assertEquals("<html>done</html>", content);
    }

    @Test
    public void getReportContent_NotFound_Throws() {
        Mockito.when(reportRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> reportService.getReportContent(99L));
    }
}
