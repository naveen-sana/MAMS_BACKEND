package com.military.asset.controller;

import com.military.asset.dto.AssignmentRequestDTO;
import com.military.asset.model.Assignment;
import com.military.asset.service.AssetManagementService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin(origins = "*")
public class AssignmentController {

    private final AssetManagementService assetManagementService;

    public AssignmentController(AssetManagementService assetManagementService) {
        this.assetManagementService = assetManagementService;
    }

    @PostMapping
    public ResponseEntity<Assignment> createAssignment(@Valid @RequestBody AssignmentRequestDTO dto) {
        return new ResponseEntity<>(assetManagementService.createAssignment(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Assignment>> getAssignments(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(assetManagementService.getAssignments(baseId, equipmentTypeId, startDate, endDate));
    }
}
