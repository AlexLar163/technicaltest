package com.alargo.account_movements_microservice.services;

import com.alargo.account_movements_microservice.entity.Account;
import com.alargo.account_movements_microservice.entity.Movement;
import com.alargo.account_movements_microservice.exception.CustomException;
import com.alargo.account_movements_microservice.exception.ResourceNotFoundException;
import com.alargo.account_movements_microservice.repository.AccountRepository;
import com.alargo.account_movements_microservice.repository.MovementRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MovementService {

    private final MovementRepository movementRepository;
    private final RabbitTemplate rabbitTemplate;
    private final AccountRepository accountRepository;

    @Autowired
    public MovementService(MovementRepository movementRepository, RabbitTemplate rabbitTemplate, AccountRepository accountRepository) {
        this.movementRepository = movementRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.accountRepository = accountRepository;
    }


    public List<Movement> getAllMovements() {
        try {
            if (movementRepository.findAll().isEmpty()) {
                throw new ResourceNotFoundException("No hay movimientos registrados");
            }

            return movementRepository.findAll();
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Error al obtener todos los movimientos: " + e.getMessage());
        }
    }

    public Movement getMovementById(Long id) {
        try {
            if (!movementRepository.existsById(id)) {
                throw new ResourceNotFoundException("Movimiento no encontrado");
            }
            return movementRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado"));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Error al obtener el movimiento por ID: " + e.getMessage());
        }
    }

    public Movement saveMovement(Movement movement) {
        try {
            Long accountId = movement.getAccount().getId();
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con ID: " + accountId));
            movement.setAccount(account);
            return movementRepository.save(movement);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Error al guardar el movimiento: " + e.getMessage());
        }
    }

    public void deleteMovement(Long id) {
        try {
            if (!movementRepository.existsById(id)) {
                throw new ResourceNotFoundException("Movimiento no encontrado");
            }
            movementRepository.deleteById(id);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Error al eliminar el movimiento: " + e.getMessage());
        }
    }

    public Movement registerMovement(Movement movementDTO) {
        Movement movement = new Movement();
        Account account;

        try {
            account = accountRepository.findById(movementDTO.getAccount().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));

            if (account.getBalance() + movementDTO.getAmount() < 0) {
                throw new ResourceNotFoundException("Saldo insuficiente");
            }

            movement.setAccount(account);
            movement.setDate(new Date());
            movement.setTypeMovement(movementDTO.getTypeMovement());
            movement.setAmount(movementDTO.getAmount());
            movement.setInitialBalance(account.getBalance() + movementDTO.getAmount());

            account.setBalance(movement.getInitialBalance());

            accountRepository.save(account);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Error al registrar movimiento: " + e.getMessage());
        }
        return movementRepository.save(movement);
    }
}


