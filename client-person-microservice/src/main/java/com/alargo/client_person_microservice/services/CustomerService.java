package com.alargo.client_person_microservice.services;

import com.alargo.client_person_microservice.dto.CustomerFilterDTO;
import com.alargo.client_person_microservice.dto.ReportDTO;
import com.alargo.client_person_microservice.entity.Customer;
import com.alargo.client_person_microservice.exception.CustomException;
import com.alargo.client_person_microservice.exception.ResourceNotFoundException;
import com.alargo.client_person_microservice.repository.CustomerRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Value("${rabbitmq.routingkey.report}")
    private String rabbitmqRoutingKeyReport;
    @Value("${rabbitmq.exchange}")
    private String rabbitmqExchange;

    private final CustomerRepository customerRepository;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, RabbitTemplate rabbitTemplate) {
        this.customerRepository = customerRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<Customer> getAllCustomers() {
        try{
            List<Customer> customers = customerRepository.findAll();
            if (customers.isEmpty()) {
                throw new ResourceNotFoundException("No hay clientes registrados");
            }
            return customers;

        } catch (Exception e) {
            throw new CustomException("Error al obtener todos los movimientos");
        }
    }

    public Customer getCustomerById(Long id) {
        try {
            return customerRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        } catch (Exception e) {
            throw new CustomException("Error al obtener el cliente por ID");
        }
    }

    public Customer saveCustomer(Customer customer) {
        try {
            return customerRepository.save(customer);
        } catch (Exception e) {
            throw new CustomException("Error al guardar el cliente");
        }
    }

    public void deleteCustomer(Long id) {
        try {
            Customer customer = getCustomerById(id);
            customerRepository.delete(customer);
        } catch (Exception e) {
            throw new CustomException("Error al eliminar el cliente");
        }
    }


    public void processReportRequest(ReportDTO reportDTO) {
        try {
            Long customerId = reportDTO.getId();
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
            CustomerFilterDTO customerDataDTO = new CustomerFilterDTO();
            customerDataDTO.setId(customer.getId());
            customerDataDTO.setName(customer.getName());
            customerDataDTO.setStartDate(reportDTO.getStartDate());
            customerDataDTO.setFinishDate(reportDTO.getFinishDate());
            rabbitTemplate.convertAndSend(rabbitmqExchange, rabbitmqRoutingKeyReport, customerDataDTO);
        } catch (Exception e) {
            throw new CustomException("Error al generar el reporte: " + e.getMessage());
        }
    }
}