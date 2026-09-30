package com.military.asset.repository;

import com.military.asset.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    @Query("SELECT t FROM Transfer t WHERE (:baseId IS NULL OR t.fromBase.id = :baseId OR t.toBase.id = :baseId) " +
           "AND (:equipmentTypeId IS NULL OR t.equipmentType.id = :equipmentTypeId) " +
           "AND (:startDate IS NULL OR t.date >= :startDate) " +
           "AND (:endDate IS NULL OR t.date <= :endDate) ORDER BY t.id DESC")
    List<Transfer> filterTransfers(@Param("baseId") Long baseId,
                                  @Param("equipmentTypeId") Long equipmentTypeId,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM Transfer t WHERE t.toBase.id = :baseId AND t.equipmentType.id = :equipmentTypeId")
    int sumTransfersInByBaseAndEquipmentType(@Param("baseId") Long baseId, @Param("equipmentTypeId") Long equipmentTypeId);

    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM Transfer t WHERE t.fromBase.id = :baseId AND t.equipmentType.id = :equipmentTypeId")
    int sumTransfersOutByBaseAndEquipmentType(@Param("baseId") Long baseId, @Param("equipmentTypeId") Long equipmentTypeId);
}
