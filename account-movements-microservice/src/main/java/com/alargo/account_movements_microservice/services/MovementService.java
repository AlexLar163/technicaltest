package com.alargo.account_movements_microservice.services;

import com.alargo.account_movements_microservice.entity.Movement;
import com.alargo.account_movements_microservice.repository.MovementRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovementService {

    private final MovementRepository movementRepository;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MovementService(MovementRepository movementRepository, RabbitTemplate rabbitTemplate) {
        this.movementRepository = movementRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<Movement> getAllMovements() {
        return movementRepository.findAll();
    }

    public Movement getMovementById(Long id) {
        return movementRepository.findById(id).orElse(null);
    }

    public Movement saveMovement(Movement movement) {
        Movement savedMovement = movementRepository.save(movement);
        rabbitTemplate.convertAndSend("movements.exchange", "movements.created", savedMovement);
        return savedMovement;
    }

    public void deleteMovement(Long id) {
        movementRepository.deleteById(id);
    }
}


