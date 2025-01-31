package com.alargo.account_movements_microservice.services;

import com.alargo.account_movements_microservice.entity.Account;
import com.alargo.account_movements_microservice.entity.Movement;
import com.alargo.account_movements_microservice.exception.CustomException;
import com.alargo.account_movements_microservice.exception.ResourceNotFoundException;
import com.alargo.account_movements_microservice.repository.AccountRepository;
import com.alargo.account_movements_microservice.repository.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class MovementServiceTests {

    @Mock
    private MovementRepository movementRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private MovementService movementService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllMovements() {
        // Arrange
        Movement movement1 = new Movement();
        Movement movement2 = new Movement();
        List<Movement> movements = List.of(movement1, movement2);
        when(movementRepository.findAll()).thenReturn(movements);

        // Act
        List<Movement> result = movementService.getAllMovements();

        // Assert
        assertEquals(2, result.size());
        assertEquals(movements, result);
    }

    @Test
    public void testGetAllMovements_Empty() {
        // Arrange
        when(movementRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> movementService.getAllMovements());
        assertEquals("No hay movimientos registrados", exception.getMessage());
    }

    @Test
    public void testGetMovementById() {
        // Arrange
        Movement movement = new Movement();
        when(movementRepository.findById(1L)).thenReturn(Optional.of(movement));

        // Act
        Movement result = movementService.getMovementById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(movement, result);
    }

    @Test
    public void testGetMovementById_NotFound() {
        // Arrange
        when(movementRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> movementService.getMovementById(1L));
        assertEquals("Movimiento no encontrado", exception.getMessage());
    }

    @Test
    public void testSaveMovement() {
        // Arrange
        Movement movement = new Movement();
        Account account = new Account();
        account.setId(1L);
        movement.setAccount(account);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(movementRepository.save(movement)).thenReturn(movement);

        // Act
        Movement result = movementService.saveMovement(movement);

        // Assert
        assertNotNull(result);
        assertEquals(movement, result);
    }

    @Test
    public void testSaveMovement_AccountNotFound() {
        // Arrange
        Movement movement = new Movement();
        Account account = new Account();
        account.setId(1L);
        movement.setAccount(account);
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> movementService.saveMovement(movement));
        assertEquals("Cuenta no encontrada con ID: 1", exception.getMessage());
    }

    @Test
    public void testDeleteMovement() {
        // Arrange
        Movement movement = new Movement();
        when(movementRepository.findById(1L)).thenReturn(Optional.of(movement));

        // Act
        assertDoesNotThrow(() -> movementService.deleteMovement(1L));

        // Assert
        verify(movementRepository, times(1)).delete(movement);
    }

    @Test
    public void testDeleteMovement_NotFound() {
        // Arrange
        when(movementRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> movementService.deleteMovement(1L));
        assertEquals("Movimiento no encontrado", exception.getMessage());
    }


    @Test
    public void testRegisterMovement_Success() {
        // Arrange
        Movement movementDTO = new Movement();
        Account account = new Account();
        account.setId(1L);
        account.setBalance(100.0);
        movementDTO.setAccount(account);
        movementDTO.setAmount(50.0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(movementRepository.save(any(Movement.class))).thenReturn(movementDTO);
        when(accountRepository.save(account)).thenReturn(account);

        // Act
        Movement result = movementService.registerMovement(movementDTO);

        // Assert
        assertNotNull(result);
        assertEquals(movementDTO, result);
    }

    @Test
    public void testRegisterMovement_AccountNotFound() {
        // Arrange
        Movement movementDTO = new Movement();
        Account account = new Account();
        account.setId(1L);
        movementDTO.setAccount(account);

        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> movementService.registerMovement(movementDTO));
        assertEquals("Cuenta no encontrada", exception.getMessage());
    }

    @Test
    public void testRegisterMovement_InsufficientBalance() {
        // Arrange
        Movement movementDTO = new Movement();
        Account account = new Account();
        account.setId(1L);
        account.setBalance(100.0);
        movementDTO.setAccount(account);
        movementDTO.setAmount(-150.0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> movementService.registerMovement(movementDTO));
        assertEquals("Saldo insuficiente", exception.getMessage());
    }

    @Test
    public void testRegisterMovement_OtherException() {
        // Arrange
        Movement movementDTO = new Movement();
        Account account = new Account();
        account.setId(1L);
        movementDTO.setAccount(account);

        when(accountRepository.findById(1L)).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> movementService.registerMovement(movementDTO));
        assertEquals("Error al registrar movimiento", exception.getMessage());
    }

}
