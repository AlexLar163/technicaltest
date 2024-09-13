package com.alargo.account_movements_microservice.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerFilterDTO {
    private Long id;
    private Date startDate;
    private Date finishDate;
}