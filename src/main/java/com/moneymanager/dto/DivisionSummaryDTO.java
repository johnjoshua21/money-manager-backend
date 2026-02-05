package com.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DivisionSummaryDTO {
    private Map<String, DivisionData> divisions;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DivisionData {
        private Double income;
        private Double expense;
        private Double balance;
    }
}
