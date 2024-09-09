package com.alargo.account_movements_microservice.services;

import com.alargo.account_movements_microservice.entity.Account;
import com.alargo.account_movements_microservice.exception.CustomException;
import com.alargo.account_movements_microservice.exception.ResourceNotFoundException;
import com.alargo.account_movements_microservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    public List<Account> getAllAccounts() {
        try {
            if (accountRepository.findAll().isEmpty()) {
                throw new ResourceNotFoundException("No hay cuentas registradas");
            }

            return accountRepository.findAll();
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Error al obtener todas las cuentas: " + e.getMessage());
        }
    }

    public Account getAccountById(Long id) {
        try {
            if (!accountRepository.existsById(id)) {
                throw new ResourceNotFoundException("Cuenta no encontrada");
            }
            return accountRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Error al obtener la cuenta por ID: " + e.getMessage());
        }
    }

    public Account saveAccount(Account account) {
        try {
            return accountRepository.save(account);
        } catch (Exception e) {
            throw new CustomException("Error al guardar la cuenta: " + e.getMessage());
        }
    }

    public void deleteAccount(Long id) {
        try {
            if (!accountRepository.existsById(id)) {
                throw new ResourceNotFoundException("Cuenta no encontrada");
            }
            accountRepository.deleteById(id);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Error al eliminar la cuenta: " + e.getMessage());
        }
    }

}


