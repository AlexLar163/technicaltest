package com.alargo.client_person_microservice.listener;

import com.alargo.client_person_microservice.dto.CustomerFilterDTO;
import com.alargo.client_person_microservice.dto.ReportDTO;
import com.alargo.client_person_microservice.entity.Customer;
import com.alargo.client_person_microservice.exception.ResourceNotFoundException;
import com.alargo.client_person_microservice.repository.CustomerRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Component
public class CustomerListener {

    private final CustomerRepository customerRepository;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public CustomerListener(CustomerRepository customerRepository, RabbitTemplate rabbitTemplate) {
        this.customerRepository = customerRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = {"customer.queue"})
    public void requestReport(@Payload ReportDTO reportDTO) {
        try {
            Long customerId = reportDTO.getId();
            Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
            CustomerFilterDTO customerDataDTO = new CustomerFilterDTO();
            customerDataDTO.setId(customer.getId());
            customerDataDTO.setName(customer.getName());
            customerDataDTO.setStartDate(reportDTO.getStartDate());
            customerDataDTO.setFinishDate(reportDTO.getFinishDate());
            rabbitTemplate.convertAndSend("exchange_name", "routing_key_report", customerDataDTO);
        }
        catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error al obtener el cliente por ID: " + e.getMessage());
        }
    }
}
