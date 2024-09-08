package com.alargo.account_movements_microservice.controller;

import com.alargo.account_movements_microservice.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;


@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }


    // Endpoint para generar reporte de estado de cuenta
    @GetMapping
    public ResponseEntity<Map<String, Object>> generarReporte(
            @RequestParam("fechaInicio") Date fechaInicio,
            @RequestParam("fechaFin") Date fechaFin,
            @RequestParam("clienteId") Long clienteId) {

        // Delegar la generación del reporte al servicio
        Map<String, Object> reporte = reportService.generarReporte(fechaInicio, fechaFin, clienteId);

        return ResponseEntity.ok(reporte);
    }
}
