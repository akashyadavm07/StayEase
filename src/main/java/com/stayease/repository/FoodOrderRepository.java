package com.stayease.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.stayease.model.FoodOrder;

public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {

    @EntityGraph(attributePaths = "items")
    List<FoodOrder> findAllByOrderByCreatedAtDesc();

    Optional<FoodOrder> findByIdAndRoomNumber(Long id, String roomNumber);

}