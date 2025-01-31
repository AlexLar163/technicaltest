
package com.alargo.client_person_microservice.services;

import com.alargo.client_person_microservice.dto.CustomerFilterDTO;
import com.alargo.client_person_microservice.dto.ReportDTO;
import com.alargo.client_person_microservice.entity.Customer;
import com.alargo.client_person_microservice.exception.CustomException;
import com.alargo.client_person_microservice.exception.ResourceNotFoundException;
import com.alargo.client_person_microservice.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource("classpath:application.properties")
public class CustomerServiceTests {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private CustomerService customerService;

    @Value("${rabbitmq.routingkey.report}")
    private String rabbitmqRoutingKeyReport;

    @Value("${rabbitmq.exchange}")
    private String rabbitmqExchange;

    private Customer customer;

    @BeforeEach
    public void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
    }

    @Test
    public void testGetAllCustomers_Success() {
        // Arrange
        List<Customer> customers = Collections.singletonList(customer);
        when(customerRepository.findAll()).thenReturn(customers);

        // Act
        List<Customer> result = customerService.getAllCustomers();

        // Assert
        assertEquals(1, result.size());

        // Verify
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllCustomers_NoCustomers() {
        // Arrange
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            customerService.getAllCustomers();
        });

        // Assert
        assertEquals("No hay clientes registrados", exception.getMessage());
    }

    @Test
    public void testGetAllCustomers_Exception() {
        // Arrange
        when(customerRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act
        CustomException exception = assertThrows(CustomException.class, () -> {
            customerService.getAllCustomers();
        });

        // Assert
        assertEquals("Error al obtener todos los clientes", exception.getMessage());
    }

    @Test
    public void testGetCustomerById_Success() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // Act
        Customer result = customerService.getCustomerById(1L);

        // Assert
        assertEquals(customer.getId(), result.getId());

        // Verify
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetCustomerById_NotFound() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            customerService.getCustomerById(1L);
        });

        // Assert
        assertEquals("Cliente no encontrado", exception.getMessage());
    }

    @Test
    public void testGetCustomerById_Exception() {
        // Arrange
        when(customerRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        // Act
        CustomException exception = assertThrows(CustomException.class, () -> {
            customerService.getCustomerById(1L);
        });

        // Assert
        assertEquals("Error al obtener el cliente por ID", exception.getMessage());
    }

    @Test
    public void testSaveCustomer_Success() {
        // Arrange
        when(customerRepository.save(customer)).thenReturn(customer);

        // Act
        Customer result = customerService.saveCustomer(customer);

        // Assert
        assertEquals(customer.getId(), result.getId());

        // Verify
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    public void testSaveCustomer_Exception() {
        // Arrange
        when(customerRepository.save(customer)).thenThrow(new RuntimeException("Database error"));

        // Act
        CustomException exception = assertThrows(CustomException.class, () -> {
            customerService.saveCustomer(customer);
        });

        // Assert
        assertEquals("Error al guardar el cliente", exception.getMessage());
    }

    @Test
    public void testDeleteCustomer_Success() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // Act
        customerService.deleteCustomer(1L);

        // Verify
        verify(customerRepository, times(1)).delete(customer);
    }

    @Test
    public void testDeleteCustomer_Exception() {
        // Arrange
        when(customerRepository.findById(anyLong())).thenThrow(new RuntimeException("Database error"));

        // Act
        CustomException exception = assertThrows(CustomException.class, () -> {
            customerService.deleteCustomer(1L);
        });

        // Assert
        assertEquals("Error al eliminar el cliente", exception.getMessage());
    }

    @Test
    public void testProcessReportRequest_Success() {
        // Arrange
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setId(1L);
        reportDTO.setStartDate(Date.valueOf("2023-01-01"));
        reportDTO.setFinishDate(Date.valueOf("2023-12-31"));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerFilterDTO customerFilterDTO = new CustomerFilterDTO();
        customerFilterDTO.setId(customer.getId());
        customerFilterDTO.setName(customer.getName());
        customerFilterDTO.setStartDate(reportDTO.getStartDate());
        customerFilterDTO.setFinishDate(reportDTO.getFinishDate());

        doNothing().when(rabbitTemplate).convertAndSend(rabbitmqExchange, rabbitmqRoutingKeyReport, customerFilterDTO);

        // Act
        assertDoesNotThrow(() -> customerService.processReportRequest(reportDTO));

        // Verify
        verify(rabbitTemplate, times(1)).convertAndSend(eq(rabbitmqExchange), eq(rabbitmqRoutingKeyReport), any(CustomerFilterDTO.class));
    }

    @Test
    public void testProcessReportRequest_CustomerNotFound() {
        // Arrange
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setId(1L);
        reportDTO.setStartDate(Date.valueOf("2023-01-01"));
        reportDTO.setFinishDate(Date.valueOf("2023-12-31"));
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            customerService.processReportRequest(reportDTO);
        });

        // Assert
        assertEquals("Cliente no encontrado", exception.getMessage());
    }

    @Test
    public void testProcessReportRequest_Exception() {
        // Arrange
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setId(1L);
        reportDTO.setStartDate(Date.valueOf("2023-01-01"));
        reportDTO.setFinishDate(Date.valueOf("2023-12-31"));
        when(customerRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        // Act
        CustomException exception = assertThrows(CustomException.class, () -> {
            customerService.processReportRequest(reportDTO);
        });

        // Assert
        assertTrue(exception.getMessage().startsWith("Error al generar el reporte:"));
    }
}