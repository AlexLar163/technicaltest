package com.alargo.account_movements_microservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Entity
public class Movement implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    private String typeMovement;
    private double amount;
    private double initialBalance;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
