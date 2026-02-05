package com.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDTO {
    private Double totalIncome;
    private Double totalExpense;
    private Double balance;
    private String period;
    private String periodLabel;
}
