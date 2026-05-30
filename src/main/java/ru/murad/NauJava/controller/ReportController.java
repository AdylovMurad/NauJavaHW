package ru.murad.NauJava.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.murad.NauJava.service.ReportService;

@RestController
@RequestMapping("/report")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/generate")
    public String generateReport() {
        Long id = reportService.createReport();
        reportService.generateReportAsync(id);
        logger.info("REST report generation requested id={}", id);
        return "Отчет формируется! Запомните ID: " + id +
                ". Проверить результат можно по адресу: /report/" + id;
    }

    @GetMapping(value = "/{id}", produces = "text/html;charset=UTF-8")
    public String getReport(@PathVariable Long id) {
        return reportService.getReportContent(id);
    }
}