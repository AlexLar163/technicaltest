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
            List<Account> accounts = accountRepository.findAll();
            if (accounts.isEmpty()) {
                throw new ResourceNotFoundException("No hay cuentas registradas");
            }
            return accounts;
        } catch (Exception e) {
            throw new CustomException("Error al obtener todas las cuentas");
        }
    }

    public Account getAccountById(Long id) {
        try {
            return accountRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));
        } catch (Exception e) {
            throw new CustomException("Error al obtener la cuenta por ID");
        }
    }

    public Account saveAccount(Account account) {
        try {
            return accountRepository.save(account);
        } catch (Exception e) {
            throw new CustomException("Error al guardar la cuenta");
        }
    }

    public void deleteAccount(Long id) {
        try {
            Account account = getAccountById(id);
            accountRepository.delete(account);
        } catch (Exception e) {
            throw new CustomException("Error al eliminar la cuenta");
        }
    }
}