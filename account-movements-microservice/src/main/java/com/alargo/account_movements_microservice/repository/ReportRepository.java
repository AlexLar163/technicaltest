package com.alargo.account_movements_microservice.repository;

import com.alargo.account_movements_microservice.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByAccount_Id(Long accountId);

}


