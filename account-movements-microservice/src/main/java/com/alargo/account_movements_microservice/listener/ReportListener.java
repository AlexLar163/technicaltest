package com.alargo.account_movements_microservice.listener;

import com.alargo.account_movements_microservice.dto.CustomerDataDTO;
import com.alargo.account_movements_microservice.services.ReportService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Component
public class ReportListener {
    private final ReportService reportService;

    @Autowired
    public ReportListener(ReportService reportService) {
        this.reportService = reportService;
    }

    @RabbitListener(queues = {"report.queue"})
    public void onCustomerResponse(@Payload CustomerDataDTO customerData) {
        System.out.println("222222222222  Received customer data: " + customerData);
        reportService.saveReport(customerData);
    }
}