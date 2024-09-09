package com.alargo.account_movements_microservice.listener;

import com.alargo.account_movements_microservice.dto.CustomerFilterDTO;
import com.alargo.account_movements_microservice.entity.Account;
import com.alargo.account_movements_microservice.entity.Movement;
import com.alargo.account_movements_microservice.entity.Report;
import com.alargo.account_movements_microservice.repository.AccountRepository;
import com.alargo.account_movements_microservice.repository.MovementRepository;
import com.alargo.account_movements_microservice.repository.ReportRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReportListener {
    private final ReportRepository reportRepository;
    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;

    @Autowired
    public ReportListener(AccountRepository accountRepository, MovementRepository movementRepository, ReportRepository reportRepository) {
        this.accountRepository = accountRepository;
        this.movementRepository = movementRepository;
        this.reportRepository = reportRepository;
    }

    @RabbitListener(queues = "queue_name_report")
    public void onCustomerResponse(CustomerFilterDTO customerFilter) {
        Long customerId = customerFilter.getId();
        String customerName = customerFilter.getName();

        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        List<Report> reports = new ArrayList<>();

        for (Account account : accounts) {
            List<Movement> movements = movementRepository
                    .findByAccountAndDateBetween(account, customerFilter.getStartDate(), customerFilter.getFinishDate());

            for (Movement movement : movements) {
                Report report = new Report();
                report.setCustomerName(customerName);
                report.setAccount(account);
                report.setMovement(movement);
                report.setAvailableBalance(movement.getInitialBalance() + movement.getAmount());

                reports.add(report);
            }
        }

        reportRepository.saveAll(reports);
    }
}