package com.alargo.client_person_microservice.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public class Person {
    private String name;
    private String gender;
    private int age;
    private String identification;
    private String address;
    private String phone;
}
