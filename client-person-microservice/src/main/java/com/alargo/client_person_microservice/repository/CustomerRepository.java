package com.alargo.client_person_microservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.alargo.client_person_microservice.entity.Customer;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
