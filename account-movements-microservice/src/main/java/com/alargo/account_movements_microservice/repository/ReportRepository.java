package com.alargo.account_movements_microservice.repository;

import com.alargo.account_movements_microservice.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByAccount_Id(Long accountId);

}


