package com.military.asset.controller;

import com.military.asset.dto.ExpenditureRequestDTO;
import com.military.asset.model.Expenditure;
import com.military.asset.service.AssetManagementService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenditures")
@CrossOrigin(origins = "*")
public class ExpenditureController {

    private final AssetManagementService assetManagementService;

    public ExpenditureController(AssetManagementService assetManagementService) {
        this.assetManagementService = assetManagementService;
    }

    @PostMapping
    public ResponseEntity<Expenditure> createExpenditure(@Valid @RequestBody ExpenditureRequestDTO dto) {
        return new ResponseEntity<>(assetManagementService.createExpenditure(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Expenditure>> getExpenditures(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(assetManagementService.getExpenditures(baseId, equipmentTypeId, startDate, endDate));
    }
}
