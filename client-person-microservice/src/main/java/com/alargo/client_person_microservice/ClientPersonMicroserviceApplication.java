package com.alargo.client_person_microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.alargo.client_person_microservice")
public class ClientPersonMicroserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientPersonMicroserviceApplication.class, args);
    }
}
