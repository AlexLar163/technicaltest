package com.alargo.account_movements_microservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private double availableBalance;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "movement_id")
    private Movement movement;

}
