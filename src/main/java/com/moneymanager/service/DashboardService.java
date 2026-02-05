package com.moneymanager.service;

import com.moneymanager.dto.CategorySummaryDTO;
import com.moneymanager.dto.ChartDataDTO;
import com.moneymanager.dto.DashboardSummaryDTO;
import com.moneymanager.dto.DivisionSummaryDTO;
import com.moneymanager.model.Transaction;
import com.moneymanager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final TransactionRepository transactionRepository;
    
    public DashboardSummaryDTO getDashboardSummary(String period, LocalDateTime date) {
        LocalDateTime startDate;
        LocalDateTime endDate;
        String periodLabel;
        
        switch (period.toUpperCase()) {
            case "WEEKLY":
                startDate = date.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).toLocalDate().atStartOfDay();
                endDate = startDate.plusDays(7).minusSeconds(1);
                periodLabel = "Week of " + startDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
                break;
            case "YEARLY":
                startDate = LocalDateTime.of(date.getYear(), 1, 1, 0, 0);
                endDate = LocalDateTime.of(date.getYear(), 12, 31, 23, 59, 59);
                periodLabel = String.valueOf(date.getYear());
                break;
            case "MONTHLY":
            default:
                YearMonth yearMonth = YearMonth.from(date);
                startDate = yearMonth.atDay(1).atStartOfDay();
                endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
                periodLabel = date.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
                break;
        }
        
        List<Transaction> transactions = transactionRepository.findByDateBetween(startDate, endDate);
        
        double totalIncome = transactions.stream()
            .filter(t -> t.getType() == Transaction.TransactionType.INCOME)
            .mapToDouble(Transaction::getAmount)
            .sum();
        
        double totalExpense = transactions.stream()
            .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
            .mapToDouble(Transaction::getAmount)
            .sum();
        
        return new DashboardSummaryDTO(
            totalIncome,
            totalExpense,
            totalIncome - totalExpense,
            period,
            periodLabel
        );
    }
    
    public ChartDataDTO getChartData(String period, int year) {
        List<String> labels = new ArrayList<>();
        List<Double> incomeData = new ArrayList<>();
        List<Double> expenseData = new ArrayList<>();
        
        switch (period.toUpperCase()) {
            case "WEEKLY":
                // Get data for each week of the year
                for (int week = 1; week <= 52; week++) {
                    labels.add("Week " + week);
                    LocalDateTime weekStart = LocalDateTime.of(year, 1, 1, 0, 0)
                        .with(WeekFields.of(Locale.getDefault()).weekOfYear(), week);
                    LocalDateTime weekEnd = weekStart.plusDays(7);
                    
                    Map<Transaction.TransactionType, Double> weekData = getTransactionSumByType(weekStart, weekEnd);
                    incomeData.add(weekData.getOrDefault(Transaction.TransactionType.INCOME, 0.0));
                    expenseData.add(weekData.getOrDefault(Transaction.TransactionType.EXPENSE, 0.0));
                }
                break;
                
            case "YEARLY":
                // Get data for last 5 years
                for (int y = year - 4; y <= year; y++) {
                    labels.add(String.valueOf(y));
                    LocalDateTime yearStart = LocalDateTime.of(y, 1, 1, 0, 0);
                    LocalDateTime yearEnd = LocalDateTime.of(y, 12, 31, 23, 59, 59);
                    
                    Map<Transaction.TransactionType, Double> yearData = getTransactionSumByType(yearStart, yearEnd);
                    incomeData.add(yearData.getOrDefault(Transaction.TransactionType.INCOME, 0.0));
                    expenseData.add(yearData.getOrDefault(Transaction.TransactionType.EXPENSE, 0.0));
                }
                break;
                
            case "MONTHLY":
            default:
                // Get data for each month of the year
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                for (int month = 1; month <= 12; month++) {
                    labels.add(months[month - 1]);
                    YearMonth yearMonth = YearMonth.of(year, month);
                    LocalDateTime monthStart = yearMonth.atDay(1).atStartOfDay();
                    LocalDateTime monthEnd = yearMonth.atEndOfMonth().atTime(23, 59, 59);
                    
                    Map<Transaction.TransactionType, Double> monthData = getTransactionSumByType(monthStart, monthEnd);
                    incomeData.add(monthData.getOrDefault(Transaction.TransactionType.INCOME, 0.0));
                    expenseData.add(monthData.getOrDefault(Transaction.TransactionType.EXPENSE, 0.0));
                }
                break;
        }
        
        return new ChartDataDTO(labels, incomeData, expenseData);
    }
    
    public List<CategorySummaryDTO> getCategorySummary(
            LocalDateTime startDate, 
            LocalDateTime endDate,
            Transaction.TransactionType type) {
        
        List<Transaction> transactions = transactionRepository.findByDateBetweenAndType(startDate, endDate, type);
        
        // Group by category and sum amounts
        Map<String, Double> categoryTotals = transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getCategory,
                Collectors.summingDouble(Transaction::getAmount)
            ));
        
        double total = categoryTotals.values().stream().mapToDouble(Double::doubleValue).sum();
        
        return categoryTotals.entrySet().stream()
            .map(entry -> new CategorySummaryDTO(
                entry.getKey(),
                entry.getValue(),
                total > 0 ? (entry.getValue() / total) * 100 : 0.0
            ))
            .sorted((a, b) -> Double.compare(b.getAmount(), a.getAmount()))
            .collect(Collectors.toList());
    }
    
    public DivisionSummaryDTO getDivisionSummary(LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = transactionRepository.findByDateBetween(startDate, endDate);
        
        Map<String, DivisionSummaryDTO.DivisionData> divisions = new HashMap<>();
        
        for (Transaction.Division division : Transaction.Division.values()) {
            double income = transactions.stream()
                .filter(t -> t.getDivision() == division && t.getType() == Transaction.TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();
            
            double expense = transactions.stream()
                .filter(t -> t.getDivision() == division && t.getType() == Transaction.TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
            
            divisions.put(division.name(), new DivisionSummaryDTO.DivisionData(income, expense, income - expense));
        }
        
        return new DivisionSummaryDTO(divisions);
    }
    
    private Map<Transaction.TransactionType, Double> getTransactionSumByType(
            LocalDateTime startDate, 
            LocalDateTime endDate) {
        
        List<Transaction> transactions = transactionRepository.findByDateBetween(startDate, endDate);
        
        return transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getType,
                Collectors.summingDouble(Transaction::getAmount)
            ));
    }
}
