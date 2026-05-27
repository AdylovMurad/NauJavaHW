package ru.murad.NauJava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.murad.NauJava.entity.Book;
import ru.murad.NauJava.entity.Report;
import ru.murad.NauJava.entity.ReportStatus;
import ru.murad.NauJava.repository.BookRepository;
import ru.murad.NauJava.repository.ReportRepository;
import ru.murad.NauJava.repository.UserRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository,
                         UserRepository userRepository,
                         BookRepository bookRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public Long createReport() {
        Report report = new Report(ReportStatus.CREATED);
        report = reportRepository.save(report);
        return report.getId();
    }

    public String getReportContent(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Отчет не найден"));

        if (report.getStatus() == ReportStatus.CREATED) {
            return "<html><body><h3>Отчет еще формируется. Подождите</h3></body></html>";
        }
        if (report.getStatus() == ReportStatus.ERROR) {
            return "<html><body><h3 style='color: red;'>Ошибка при формировании отчета</h3></body></html>";
        }

        return report.getContent();
    }

    @Async
    public void generateReportAsync(Long reportId) {
        CompletableFuture.runAsync(() -> {
            long totalStartTime = System.currentTimeMillis();

            AtomicLong userCount = new AtomicLong();
            AtomicLong userTime = new AtomicLong();

            AtomicReference<List<Book>> booksRef = new AtomicReference<>();
            AtomicLong booksTime = new AtomicLong();

            Thread userThread = new Thread(() -> {
                long start = System.currentTimeMillis();
                userCount.set(userRepository.count());
                userTime.set(System.currentTimeMillis() - start);
            });

            Thread booksThread = new Thread(() -> {
                long start = System.currentTimeMillis();
                booksRef.set((List<Book>) bookRepository.findAll());
                booksTime.set(System.currentTimeMillis() - start);
            });
            try {
                userThread.start();
                booksThread.start();

                userThread.join();
                booksThread.join();

                StringBuilder html = new StringBuilder();
                html.append("<html><body>");
                html.append("<h1>Отчет по статистике приложения</h1>");
                html.append("<p>Зарегистрировано пользователей: <b>").append(userCount.get()).append("</b></p>");
                html.append("<p>Время на подсчет пользователей: ").append(userTime.get()).append(" ms</p>");

                html.append("<h3>Список книг:</h3>");
                html.append("<table border='1'><tr><th>ID</th><th>Название</th></tr>");
                for (Book book : booksRef.get()) {
                    html.append("<tr><td>").append(book.getId()).append("</td><td>").append(book.getTitle()).append("</td></tr>");
                }
                html.append("</table>");

                html.append("<p>Время на получение книг: ").append(booksTime.get()).append(" ms</p>");

                long totalElapsed = System.currentTimeMillis() - totalStartTime;
                html.append("<p><b>Общее время формирования отчета: </b>").append(totalElapsed).append(" ms</p>");
                html.append("</body></html>");

                Report report = reportRepository.findById(reportId).orElseThrow();
                report.setContent(html.toString());
                report.setStatus(ReportStatus.COMPLETED);
                reportRepository.save(report);

            } catch (Exception e) {
                Report report = reportRepository.findById(reportId).orElse(null);
                if (report != null) {
                    report.setStatus(ReportStatus.ERROR);
                    reportRepository.save(report);
                }
            }
        });
    }
}