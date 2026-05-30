package ru.murad.NauJava.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.murad.NauJava.service.ReportService;

/**
 * REST-контроллер для асинхронного формирования и просмотра статистических отчетов.
 */
@RestController
@RequestMapping("/report")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;

    /**
     * Конструктор контроллера ReportController.
     *
     * @param reportService сервис управления отчетами
     */
    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Инициирует процесс асинхронного формирования отчета.
     *
     * @return текстовое сообщение с уникальным ID созданного отчета
     */
    @GetMapping("/generate")
    public String generateReport() {
        Long id = reportService.createReport();
        reportService.generateReportAsync(id);
        logger.info("REST report generation requested id={}", id);
        return "Отчет формируется! Запомните ID: " + id +
                ". Проверить результат можно по адресу: /report/" + id;
    }

    /**
     * Возвращает HTML-контент сформированного отчета по его ID.
     *
     * @param id уникальный идентификатор отчета
     * @return HTML-строка с содержимым отчета или статусом его выполнения
     */
    @GetMapping(value = "/{id}", produces = "text/html;charset=UTF-8")
    public String getReport(@PathVariable Long id) {
        return reportService.getReportContent(id);
    }
}
