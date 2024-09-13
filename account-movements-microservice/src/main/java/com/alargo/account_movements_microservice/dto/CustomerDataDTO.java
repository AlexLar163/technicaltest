package com.alargo.account_movements_microservice.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerDataDTO {
    private Long id;
    private String name;
    private Date startDate;
    private Date finishDate;
}