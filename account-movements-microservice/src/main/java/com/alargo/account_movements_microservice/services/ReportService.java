package com.alargo.account_movements_microservice.services;

import com.alargo.account_movements_microservice.dto.CustomerDataDTO;
import com.alargo.account_movements_microservice.dto.CustomerFilterDTO;
import com.alargo.account_movements_microservice.entity.Account;
import com.alargo.account_movements_microservice.entity.Movement;
import com.alargo.account_movements_microservice.entity.Report;
import com.alargo.account_movements_microservice.exception.CustomException;
import com.alargo.account_movements_microservice.exception.ResourceNotFoundException;
import com.alargo.account_movements_microservice.repository.AccountRepository;
import com.alargo.account_movements_microservice.repository.MovementRepository;
import com.alargo.account_movements_microservice.repository.ReportRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReportService {

    @Value("${rabbitmq.routingkey.customer}")
    private String rabbitmqRoutingKeyCustomer;
    @Value("${rabbitmq.exchange}")
    private String rabbitmqExchange;

    private final RabbitTemplate rabbitTemplate;
    private final AccountRepository accountRepository;
    private final ReportRepository reportRepository;
    private final MovementRepository movementRepository;

    @Autowired
    public ReportService(RabbitTemplate rabbitTemplate, AccountRepository accountRepository, ReportRepository reportRepository, MovementRepository movementRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.accountRepository = accountRepository;
        this.reportRepository = reportRepository;
        this.movementRepository = movementRepository;
    }

    public void generateReport(Long customerId, Date startDate, Date finishDate) {
        try {
            CustomerFilterDTO customerFilter = new CustomerFilterDTO();
            customerFilter.setId(customerId);
            customerFilter.setStartDate(Date.from(startDate.toInstant()));
            customerFilter.setFinishDate(Date.from(finishDate.toInstant()));
            rabbitTemplate.convertAndSend(rabbitmqExchange, rabbitmqRoutingKeyCustomer, customerFilter);
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
        } catch (NumberFormatException e) {
            throw new CustomException("El número de cuenta debe ser un número entero");
        } catch (Exception e) {
            throw new CustomException("Error al obtener los reportes por número de cuenta: " + e.getMessage());
        }
    }

    public void saveReport(CustomerDataDTO customerData) {
        try {
            Long customerId = customerData.getId();
            String customerName = customerData.getName();

            List<Account> accounts = accountRepository.findByCustomerId(customerId);
            List<Report> reports = new ArrayList<>();

            for (Account account : accounts) {
                List<Movement> movements = movementRepository
                        .findByAccountAndDateBetween(account, customerData.getStartDate(), customerData.getFinishDate());

                for (Movement movement : movements) {
                    Report report = createReport(customerName, account, movement);
                    reports.add(report);
                }
            }

            reportRepository.saveAll(reports);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (NumberFormatException e) {
            throw new CustomException("El ID del cliente debe ser un número entero");
        } catch (Exception e) {
            throw new CustomException("An error occurred while processing the customer response: " + e.getMessage());
        }
    }

    private Report createReport(String customerName, Account account, Movement movement) {
        try {
            Report report = new Report();
            report.setCustomerName(customerName);
            report.setAccount(account);
            report.setMovement(movement);
            return report;
        } catch (Exception e) {
            throw new CustomException("Error al crear el reporte: " + e.getMessage());
        }
    }
}