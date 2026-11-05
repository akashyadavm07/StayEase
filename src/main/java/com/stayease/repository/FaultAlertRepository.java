package com.stayease.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stayease.model.FaultAlert;

public interface FaultAlertRepository extends JpaRepository<FaultAlert, Long> {

    Optional<FaultAlert> findFirstByIssueCategoryAndFloorNumberAndStatusNot(
            String issueCategory,
            String floorNumber,
            String status
    );
}