package com.alargo.client_person_microservice.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerFilterDTO {
    private Long id;
    private String name;
    private Date startDate;
    private Date finishDate;
}
