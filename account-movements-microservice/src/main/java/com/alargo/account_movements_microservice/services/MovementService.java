package com.alargo.account_movements_microservice.services;

import com.alargo.account_movements_microservice.entity.Account;
import com.alargo.account_movements_microservice.entity.Movement;
import com.alargo.account_movements_microservice.exception.CustomException;
import com.alargo.account_movements_microservice.exception.ResourceNotFoundException;
import com.alargo.account_movements_microservice.repository.AccountRepository;
import com.alargo.account_movements_microservice.repository.MovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MovementService {

    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public MovementService(MovementRepository movementRepository, AccountRepository accountRepository) {
        this.movementRepository = movementRepository;
        this.accountRepository = accountRepository;
    }

    public List<Movement> getAllMovements() {
        try {
            List<Movement> movements = movementRepository.findAll();
            if (movements.isEmpty()) {
                throw new ResourceNotFoundException("No hay movimientos registrados");
            }
            return movements;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Error al obtener todos los movimientos");
        }
    }

    public Movement getMovementById(Long id) {
        try {
            return movementRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado"));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Error al obtener el movimiento por ID");
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
            throw new CustomException("Error al guardar el movimiento");
        }
    }

    public void deleteMovement(Long id) {
        try {
            Movement movement = getMovementById(id);
            movementRepository.delete(movement);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Error al eliminar el movimiento");
        }
    }

    public Movement registerMovement(Movement movementDTO) {
        try {
            Account account = accountRepository.findById(movementDTO.getAccount().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));

            if (account.getBalance() + movementDTO.getAmount() < 0) {
                throw new ResourceNotFoundException("Saldo insuficiente");
            }

            Movement movement = new Movement();
            movement.setAccount(account);
            movement.setDate(new Date());
            movement.setTypeMovement(movementDTO.getTypeMovement());
            movement.setAmount(movementDTO.getAmount());
            movement.setInitialBalance(account.getBalance() + movementDTO.getAmount());

            account.setBalance(movement.getInitialBalance());
            accountRepository.save(account);

            return movementRepository.save(movement);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Error al registrar movimiento");
        }
    }
}