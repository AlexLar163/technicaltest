package com.alargo.client_person_microservice.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ReportDTO {
    private Long id;
    private Date startDate;
    private Date finishDate;
}
