package com.alargo.account_movements_microservice.services;

import com.alargo.account_movements_microservice.dto.CustomerDataDTO;
import com.alargo.account_movements_microservice.dto.CustomerFilterDTO;
import com.alargo.account_movements_microservice.entity.Account;
import com.alargo.account_movements_microservice.entity.Movement;
import com.alargo.account_movements_microservice.entity.Report;
import com.alargo.account_movements_microservice.exception.ResourceNotFoundException;
import com.alargo.account_movements_microservice.repository.AccountRepository;
import com.alargo.account_movements_microservice.repository.MovementRepository;
import com.alargo.account_movements_microservice.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
public class ReportServiceTests {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private MovementRepository movementRepository;

    @InjectMocks
    private ReportService reportService;

    @Value("${rabbitmq.exchange}")
    private String rabbitmqExchange;

    @Value("${rabbitmq.routingkey.customer}")
    private String rabbitmqRoutingKeyCustomer;

    @BeforeEach
    public void setUp() {
        // Verifica que las propiedades se carguen
        System.out.println("rabbitmq.exchange: " + rabbitmqExchange);
        System.out.println("rabbitmq.routingkey.customer: " + rabbitmqRoutingKeyCustomer);

        assertNotNull(rabbitmqExchange, "rabbitmq.exchange debe tener un valor válido.");
        assertNotNull(rabbitmqRoutingKeyCustomer, "rabbitmq.routingkey.customer debe tener un valor válido.");
    }

    @Test
    public void testGenerateReport() {
        // Arrange
        CustomerFilterDTO customerFilterDTO = new CustomerFilterDTO();
        customerFilterDTO.setId(1L);
        customerFilterDTO.setStartDate(new Date());
        customerFilterDTO.setFinishDate(new Date());

        doNothing().when(rabbitTemplate).convertAndSend(eq(rabbitmqExchange), eq(rabbitmqRoutingKeyCustomer), any(CustomerFilterDTO.class));

        // Act
        assertDoesNotThrow(() -> reportService.generateReport(1L, new Date(), new Date()));

        // Verify
        verify(rabbitTemplate, times(1)).convertAndSend(eq(rabbitmqExchange), eq(rabbitmqRoutingKeyCustomer), any(CustomerFilterDTO.class));
    }

    @Test
    public void testGetReportsByAccountNumber() {
        // Arrange
        Report report1 = new Report();
        Report report2 = new Report();
        List<Report> reports = Arrays.asList(report1, report2);
        when(reportRepository.findByAccount_Id(1L)).thenReturn(reports);

        // Act
        List<Report> result = reportService.getReportsByAccountNumber("1");

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    public void testGetReportsByAccountNumber_NotFound() {
        // Arrange
        when(reportRepository.findByAccount_Id(1L)).thenReturn(List.of());

        // Act y Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            reportService.getReportsByAccountNumber("1");
        });
        assertEquals("No se encontraron reportes para el número de cuenta: 1", exception.getMessage());
    }

    @Test
    public void testSaveReport() {
        // Arrange
        CustomerDataDTO customerData = new CustomerDataDTO();
        customerData.setId(1L);
        customerData.setName("John Doe");
        customerData.setStartDate(new Date());
        customerData.setFinishDate(new Date());

        Account account = new Account();
        account.setId(1L);

        List<Account> accounts = List.of(account);
        Movement movement = new Movement();
        movement.setAccount(account);
        List<Movement> movements = List.of(movement);

        when(accountRepository.findByCustomerId(1L)).thenReturn(accounts);
        when(movementRepository.findByAccountAndDateBetween(any(Account.class), any(Date.class), any(Date.class))).thenReturn(movements);
        when(reportRepository.saveAll(anyList())).thenReturn(null);

        // Act y Assert
        assertDoesNotThrow(() -> reportService.saveReport(customerData));

        // Verify
        verify(reportRepository, times(1)).saveAll(anyList());
    }
}