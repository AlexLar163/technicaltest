package com.alargo.account_movements_microservice.repository;

import com.alargo.account_movements_microservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
