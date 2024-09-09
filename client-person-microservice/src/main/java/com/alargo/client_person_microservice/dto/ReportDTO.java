package com.alargo.client_person_microservice.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ReportDTO {
    private Long customerId;
    private String name;
    private Date startDate;
    private Date finishDate;
}
