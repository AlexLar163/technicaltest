package com.alargo.account_movements_microservice.services;

import com.alargo.account_movements_microservice.entity.Account;
import com.alargo.account_movements_microservice.exception.ResourceNotFoundException;
import com.alargo.account_movements_microservice.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
public class AccountServiceTests {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountService accountService;

//    @BeforeEach
//    @Transactional
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        accountRepository.deleteAll();
//    }

    @Test
    public void testGetAllAccounts() {
        Account account1 = new Account();
        Account account2 = new Account();
        account1.setId(1L);
        account1.setAccountType("Ahorros");
        account1.setBalance(1000.0);
        account2.setId(2L);
        account2.setAccountType("Corriente");
        account2.setBalance(2000.0);

        List<Account> accounts = Arrays.asList(account1, account2);
        when(accountRepository.findAll()).thenReturn(accounts);

        List<Account> result = accountService.getAllAccounts();
        assertEquals(2, result.size());
    }

    @Test
    public void testGetAllAccounts_Empty() {
        List<Account> accounts;
        when(accountRepository.findAll()).thenReturn(accounts = List.of());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            accountService.getAllAccounts();
        });

        assertEquals("No hay cuentas registradas", exception.getMessage());
    }

    @Test
    public void testGetAccountById() {
        Account account = new Account();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        Account result = accountService.getAccountById(1L);
        assertNotNull(result);
    }

    @Test
    public void testGetAccountById_NotFound() {
        Random random = new Random();
        Long randomId = random.nextLong();

        when(accountRepository.findById(randomId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            accountService.getAccountById(randomId);
        });

        assertEquals("Cuenta no encontrada", exception.getMessage());
    }

    @Test
    public void testSaveAccount() {
        Account account = new Account();
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account result = accountService.saveAccount(account);
        assertNotNull(result);
    }

    @Test
    public void testDeleteAccount() {
        Account account = new Account();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        doNothing().when(accountRepository).delete(account);

        assertDoesNotThrow(() -> accountService.deleteAccount(1L));
        verify(accountRepository, times(1)).delete(account);
    }

    @Test
    public void testDeleteAccount_NotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            accountService.deleteAccount(1L);
        });

        assertEquals("Cuenta no encontrada", exception.getMessage());
    }
}