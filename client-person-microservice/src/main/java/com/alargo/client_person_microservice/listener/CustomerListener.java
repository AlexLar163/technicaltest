package com.alargo.client_person_microservice.listener;

import com.alargo.client_person_microservice.dto.CustomerFilterDTO;
import com.alargo.client_person_microservice.dto.ReportDTO;
import com.alargo.client_person_microservice.entity.Customer;
import com.alargo.client_person_microservice.exception.ResourceNotFoundException;
import com.alargo.client_person_microservice.repository.CustomerRepository;
import com.alargo.client_person_microservice.services.CustomerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Component
public class CustomerListener {

    private final CustomerService customerService;

    @Autowired
    public CustomerListener(CustomerService customerService) {
        this.customerService = customerService;
    }

    @RabbitListener(queues = {"customer.queue"})
    public void requestReport(@Payload ReportDTO reportDTO) {
`        customerService.processReportRequest(reportDTO);
    }
}
