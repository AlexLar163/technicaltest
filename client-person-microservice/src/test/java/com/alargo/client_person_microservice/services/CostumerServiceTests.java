package com.alargo.client_person_microservice.services;

import com.alargo.client_person_microservice.entity.Customer;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CostumerServiceTests {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private com.alargo.client_person_microservice.repository.CustomerRepository customerRepository;

    @Test
    public void testSaveCustomer() {
        Customer customer = new Customer();
        customer.setName("Test");
        when(customerRepository.save(customer)).thenReturn(customer);
        Customer savedCustomer = customerService.saveCustomer(customer);
        assertEquals("Test", savedCustomer.getName());
    }
}