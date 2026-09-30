package com.military.asset.repository;

import com.military.asset.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    @Query("SELECT a FROM Assignment a WHERE (:baseId IS NULL OR a.base.id = :baseId) " +
           "AND (:equipmentTypeId IS NULL OR a.equipmentType.id = :equipmentTypeId) " +
           "AND (:startDate IS NULL OR a.date >= :startDate) " +
           "AND (:endDate IS NULL OR a.date <= :endDate) ORDER BY a.id DESC")
    List<Assignment> filterAssignments(@Param("baseId") Long baseId,
                                      @Param("equipmentTypeId") Long equipmentTypeId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(a.quantity), 0) FROM Assignment a WHERE a.base.id = :baseId AND a.equipmentType.id = :equipmentTypeId")
    int sumQuantityByBaseAndEquipmentType(@Param("baseId") Long baseId, @Param("equipmentTypeId") Long equipmentTypeId);
}
