package com.military.asset.controller;

import com.military.asset.model.Base;
import com.military.asset.model.EquipmentType;
import com.military.asset.model.PersonnelUnit;
import com.military.asset.service.AssetManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ReferenceDataController {

    private final AssetManagementService assetManagementService;

    public ReferenceDataController(AssetManagementService assetManagementService) {
        this.assetManagementService = assetManagementService;
    }

    @GetMapping("/bases")
    public ResponseEntity<List<Base>> getBases() {
        return ResponseEntity.ok(assetManagementService.getAllBases());
    }

    @PostMapping("/bases")
    public ResponseEntity<Base> createBase(@RequestBody Base base) {
        return ResponseEntity.ok(assetManagementService.createBase(base));
    }

    @PutMapping("/bases/{id}")
    public ResponseEntity<Base> updateBase(@PathVariable Long id, @RequestBody Base base) {
        return ResponseEntity.ok(assetManagementService.updateBase(id, base));
    }

    @DeleteMapping("/bases/{id}")
    public ResponseEntity<Void> deleteBase(@PathVariable Long id) {
        assetManagementService.deleteBase(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/equipment-types")
    public ResponseEntity<List<EquipmentType>> getEquipmentTypes() {
        return ResponseEntity.ok(assetManagementService.getAllEquipmentTypes());
    }

    @GetMapping("/personnel-units")
    public ResponseEntity<List<PersonnelUnit>> getPersonnelUnits() {
        return ResponseEntity.ok(assetManagementService.getAllPersonnelUnits());
    }
}
