package com.moneymanager.controller;

import com.moneymanager.dto.*;
import com.moneymanager.model.Transaction;
import com.moneymanager.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryDTO>> getDashboardSummary(
            @RequestParam(defaultValue = "MONTHLY") String period,
            @RequestParam(required = false) String date
    ) {
        LocalDateTime dateTime;

        if (date != null) {
            try {
                // Handle different date formats
                if (date.length() == 4) {
                    // Year only: "2024"
                    dateTime = LocalDateTime.of(Integer.parseInt(date), 1, 1, 0, 0);
                } else if (date.length() == 7) {
                    // Year-Month: "2024-02"
                    String[] parts = date.split("-");
                    dateTime = LocalDateTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), 1, 0, 0);
                } else if (date.length() == 10) {
                    // Date only: "2024-02-05"
                    String[] parts = date.split("-");
                    dateTime = LocalDateTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), 0, 0);
                } else {
                    // Full ISO format: "2024-02-05T10:00:00"
                    dateTime = LocalDateTime.parse(date);
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid date format. Use: YYYY, YYYY-MM, YYYY-MM-DD, or ISO 8601"));
            }
        } else {
            dateTime = LocalDateTime.now();
        }

        DashboardSummaryDTO summary = dashboardService.getDashboardSummary(period, dateTime);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/chart")
    public ResponseEntity<ApiResponse<ChartDataDTO>> getChartData(
            @RequestParam(defaultValue = "MONTHLY") String period,
            @RequestParam(required = false) Integer year
    ) {
        if (year == null) {
            year = LocalDateTime.now().getYear();
        }

        ChartDataDTO chartData = dashboardService.getChartData(period, year);
        return ResponseEntity.ok(ApiResponse.success(chartData));
    }

    @GetMapping("/category-summary")
    public ResponseEntity<ApiResponse<List<CategorySummaryDTO>>> getCategorySummary(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam String type
    ) {
        Transaction.TransactionType transactionType;
        try {
            transactionType = Transaction.TransactionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid transaction type. Use INCOME or EXPENSE"));
        }

        // Default to current month if dates not provided
        LocalDateTime start;
        LocalDateTime end;

        if (startDate != null && endDate != null) {
            try {
                start = LocalDateTime.parse(startDate);
                end = LocalDateTime.parse(endDate);
            } catch (Exception e) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid date format. Use ISO 8601: YYYY-MM-DDTHH:mm:ss"));
            }
        } else {
            // Default to current month
            LocalDateTime now = LocalDateTime.now();
            start = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            end = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);
        }

        List<CategorySummaryDTO> summary = dashboardService.getCategorySummary(start, end, transactionType);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/division-summary")
    public ResponseEntity<ApiResponse<DivisionSummaryDTO>> getDivisionSummary(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        // Default to current month if dates not provided
        LocalDateTime start;
        LocalDateTime end;

        if (startDate != null && endDate != null) {
            try {
                start = LocalDateTime.parse(startDate);
                end = LocalDateTime.parse(endDate);
            } catch (Exception e) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid date format. Use ISO 8601: YYYY-MM-DDTHH:mm:ss"));
            }
        } else {
            // Default to current month
            LocalDateTime now = LocalDateTime.now();
            start = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            end = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);
        }

        DivisionSummaryDTO summary = dashboardService.getDivisionSummary(start, end);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}