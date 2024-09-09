package com.alargo.account_movements_microservice.controller;

import com.alargo.account_movements_microservice.entity.Report;
import com.alargo.account_movements_microservice.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateReport(
            @RequestParam Long customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date finishDate) {

        reportService.generateReport(customerId, startDate, finishDate);

        return ResponseEntity.ok("Reporte solicitado. Recibirá los resultados cuando estén disponibles.");
    }

    @GetMapping("/{accountNumber}")
    public List<Report> getReportGenerated(@PathVariable String accountNumber) {
        return reportService.getReportsByAccountNumber(accountNumber);
    }


}
