package com.military.asset.repository;

import com.military.asset.model.Expenditure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

    @Query("SELECT e FROM Expenditure e WHERE (:baseId IS NULL OR e.base.id = :baseId) " +
           "AND (:equipmentTypeId IS NULL OR e.equipmentType.id = :equipmentTypeId) " +
           "AND (:startDate IS NULL OR e.date >= :startDate) " +
           "AND (:endDate IS NULL OR e.date <= :endDate) ORDER BY e.id DESC")
    List<Expenditure> filterExpenditures(@Param("baseId") Long baseId,
                                        @Param("equipmentTypeId") Long equipmentTypeId,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(e.quantity), 0) FROM Expenditure e WHERE e.base.id = :baseId AND e.equipmentType.id = :equipmentTypeId")
    int sumQuantityByBaseAndEquipmentType(@Param("baseId") Long baseId, @Param("equipmentTypeId") Long equipmentTypeId);
}
