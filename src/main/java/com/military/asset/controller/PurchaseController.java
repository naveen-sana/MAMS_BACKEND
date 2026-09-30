package com.military.asset.controller;

import com.military.asset.dto.PurchaseRequestDTO;
import com.military.asset.model.Purchase;
import com.military.asset.service.AssetManagementService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "*")
public class PurchaseController {

    private final AssetManagementService assetManagementService;

    public PurchaseController(AssetManagementService assetManagementService) {
        this.assetManagementService = assetManagementService;
    }

    @PostMapping
    public ResponseEntity<Purchase> createPurchase(@Valid @RequestBody PurchaseRequestDTO dto) {
        return new ResponseEntity<>(assetManagementService.createPurchase(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Purchase>> getPurchases(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(assetManagementService.getPurchases(baseId, equipmentTypeId, startDate, endDate));
    }
}
