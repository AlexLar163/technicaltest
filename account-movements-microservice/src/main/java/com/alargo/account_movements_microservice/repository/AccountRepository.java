package com.alargo.account_movements_microservice.repository;

import com.alargo.account_movements_microservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByCustomerId(Long customerId);
}
