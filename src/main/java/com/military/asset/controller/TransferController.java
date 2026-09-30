package com.military.asset.controller;

import com.military.asset.dto.TransferRequestDTO;
import com.military.asset.model.Transfer;
import com.military.asset.service.AssetManagementService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transfers")
@CrossOrigin(origins = "*")
public class TransferController {

    private final AssetManagementService assetManagementService;

    public TransferController(AssetManagementService assetManagementService) {
        this.assetManagementService = assetManagementService;
    }

    @PostMapping
    public ResponseEntity<Transfer> createTransfer(@Valid @RequestBody TransferRequestDTO dto) {
        return new ResponseEntity<>(assetManagementService.createTransfer(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Transfer>> getTransfers(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(assetManagementService.getTransfers(baseId, equipmentTypeId, startDate, endDate));
    }
}
