package com.military.asset.repository;

import com.military.asset.model.PersonnelUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonnelUnitRepository extends JpaRepository<PersonnelUnit, Long> {
}
