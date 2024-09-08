package com.alargo.account_movements_microservice.controller;

import com.alargo.account_movements_microservice.entity.Movement;
import com.alargo.account_movements_microservice.services.MovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movements")
public class MovementController {


    private final MovementService movementService;


    @Autowired
    public MovementController(MovementService movementService) {
        this.movementService = movementService;
    }

    @GetMapping
    public List<Movement> getAllMovements() {
        return movementService.getAllMovements();
    }

    @GetMapping("/{id}")
    public Movement getMovementById(@PathVariable Long id) {
        return movementService.getMovementById(id);
    }

    @PostMapping
    public Movement createMovement(@RequestBody Movement movement) {
        return movementService.saveMovement(movement);
    }

    @PutMapping("/{id}")
    public Movement updateMovement(@PathVariable Long id, @RequestBody Movement movement) {
        movement.setId(id);
        return movementService.saveMovement(movement);
    }

    @DeleteMapping("/{id}")
    public void deleteMovement(@PathVariable Long id) {
        movementService.deleteMovement(id);
    }
}

