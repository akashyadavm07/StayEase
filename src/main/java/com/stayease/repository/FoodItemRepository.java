
package com.stayease.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stayease.model.FoodItem;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    List<FoodItem> findByAvailableTrueOrderByNameAsc();

}
