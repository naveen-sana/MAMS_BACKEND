package com.military.asset.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TransferRequestDTO {

    @NotNull(message = "From Base ID is required")
    private Long fromBaseId;

    @NotNull(message = "To Base ID is required")
    private Long toBaseId;

    @NotNull(message = "Equipment Type ID is required")
    private Long equipmentTypeId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Date is required")
    private LocalDate date;
}
