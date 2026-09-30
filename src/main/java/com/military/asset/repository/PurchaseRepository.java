package com.military.asset.repository;

import com.military.asset.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("SELECT p FROM Purchase p WHERE (:baseId IS NULL OR p.base.id = :baseId) " +
           "AND (:equipmentTypeId IS NULL OR p.equipmentType.id = :equipmentTypeId) " +
           "AND (:startDate IS NULL OR p.date >= :startDate) " +
           "AND (:endDate IS NULL OR p.date <= :endDate) ORDER BY p.id DESC")
    List<Purchase> filterPurchases(@Param("baseId") Long baseId,
                                  @Param("equipmentTypeId") Long equipmentTypeId,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(p.quantity), 0) FROM Purchase p WHERE p.base.id = :baseId AND p.equipmentType.id = :equipmentTypeId")
    int sumQuantityByBaseAndEquipmentType(@Param("baseId") Long baseId, @Param("equipmentTypeId") Long equipmentTypeId);
}
