package ru.murad.SmartLibrary.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.murad.SmartLibrary.entity.Book;
import ru.murad.SmartLibrary.entity.Report;
import ru.murad.SmartLibrary.entity.ReportStatus;
import ru.murad.SmartLibrary.repository.BookRepository;
import ru.murad.SmartLibrary.repository.ReportRepository;
import ru.murad.SmartLibrary.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для асинхронного формирования отчетов статистики.
 */
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    /**
     * Конструктор класса ReportService.
     *
     * @param reportRepository репозиторий отчетов
     * @param userRepository   репозиторий пользователей
     * @param bookRepository   репозиторий книг
     */
    @Autowired
    public ReportService(ReportRepository reportRepository,
                         UserRepository userRepository,
                         BookRepository bookRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Создает пустую запись отчета со статусом CREATED.
     *
     * @return уникальный идентификатор созданного отчета
     */
    public Long createReport() {
        Report report = new Report(ReportStatus.CREATED);
        report = reportRepository.save(report);
        return report.getId();
    }

    /**
     * Возвращает содержимое отчета или статусную страницу, если он еще не готов.
     *
     * @param id уникальный идентификатор отчета
     * @return HTML-строка с содержимым отчета или статусом
     */
    public String getReportContent(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Отчет не найден"));

        if (report.getStatus() == ReportStatus.CREATED) {
            return "<html><body><h3>Отчет еще формируется. Подождите...</h3></body></html>";
        }
        if (report.getStatus() == ReportStatus.ERROR) {
            return "<html><body><h3 style='color: red;'>Ошибка при формировании отчета</h3></body></html>";
        }

        return report.getContent();
    }

    /**
     * Запускает асинхронную генерацию статистического отчета.
     *
     * @param reportId уникальный идентификатор отчета
     */
    @Async
    public void generateReportAsync(Long reportId) {
        try {
            long totalStartTime = System.currentTimeMillis();

            long startUser = System.currentTimeMillis();
            long userCount = userRepository.count();
            long userTime = System.currentTimeMillis() - startUser;

            long startBooks = System.currentTimeMillis();
            List<Book> books = new ArrayList<>();
            bookRepository.findAll().forEach(books::add);
            long booksTime = System.currentTimeMillis() - startBooks;

            StringBuilder html = new StringBuilder();
            html.append("<html><body>");
            html.append("<h1>Отчет по статистике приложения</h1>");
            html.append("<p>Зарегистрировано пользователей: <b>").append(userCount).append("</b></p>");
            html.append("<p>Время на подсчет пользователей: ").append(userTime).append(" ms</p>");

            html.append("<h3>Список книг:</h3>");
            html.append("<table border='1'><tr><th>ID</th><th>Название</th></tr>");
            for (Book book : books) {
                html.append("<tr><td>").append(book.getId()).append("</td><td>").append(book.getTitle()).append("</td></tr>");
            }
            html.append("</table>");

            html.append("<p>Время на получение книг: ").append(booksTime).append(" ms</p>");

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
    }
}
