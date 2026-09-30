package com.military.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummaryDTO {
    private int openingBalance;
    private int purchases;
    private int transfersIn;
    private int transfersOut;
    private int netMovement;
    private int closingBalance;
    private int assigned;
    private int expended;
}
