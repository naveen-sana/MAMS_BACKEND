package com.military.asset.repository;

import com.military.asset.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findByType(String type);
    List<Equipment> findByTypeIgnoreCase(String type);
}
