package com.alargo.account_movements_microservice.repository;

import com.alargo.account_movements_microservice.entity.Movement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovementRepository extends JpaRepository<Movement, Long> {
}


