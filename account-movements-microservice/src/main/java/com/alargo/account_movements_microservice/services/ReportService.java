package com.alargo.account_movements_microservice.services;

import com.alargo.account_movements_microservice.dto.CustomerFilterDTO;
import com.alargo.account_movements_microservice.entity.Report;
import com.alargo.account_movements_microservice.exception.CustomException;
import com.alargo.account_movements_microservice.exception.ResourceNotFoundException;
import com.alargo.account_movements_microservice.repository.AccountRepository;
import com.alargo.account_movements_microservice.repository.ReportRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReportService {
    private final RabbitTemplate rabbitTemplate;
    private final AccountRepository accountRepository;
    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(RabbitTemplate rabbitTemplate, AccountRepository accountRepository, ReportRepository reportRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.accountRepository = accountRepository;
        this.reportRepository = reportRepository;
    }

    public void generateReport(Long customerId, Date startDate, Date finishDate) {
        try {
            CustomerFilterDTO customerFilter = new CustomerFilterDTO();
            customerFilter.setId(customerId);
            customerFilter.setStartDate(Date.from(startDate.toInstant()));
            customerFilter.setFinishDate(Date.from(finishDate.toInstant()));
            rabbitTemplate.convertAndSend("exchange_name", "routing_key_customer", customerFilter);
        } catch (Exception e) {
            throw new CustomException("Error al generar el reporte: " + e.getMessage());
        }
    }

    public List<Report> getReportsByAccountNumber(String accountId) {
        try {
            List<Report> reports = reportRepository.findByAccount_Id(Long.parseLong(accountId));
            if (reports.isEmpty()) {
                throw new ResourceNotFoundException("No se encontraron reportes para el número de cuenta: " + accountId);
            }
            return reports;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Error al obtener los reportes por número de cuenta: " + e.getMessage());
        }
    }
}

