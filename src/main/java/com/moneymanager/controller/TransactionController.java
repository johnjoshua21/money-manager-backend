package com.moneymanager.controller;

import com.moneymanager.dto.ApiResponse;
import com.moneymanager.model.Transaction;
import com.moneymanager.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<Transaction>> createTransaction(@RequestBody Transaction transaction) {
        Transaction created = transactionService.createTransaction(transaction);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Transaction created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Transaction>>> getAllTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String division,
            @RequestParam(required = false) String category
    ) {
        // Convert string parameters to enums
        Transaction.TransactionType transactionType = null;
        Transaction.Division divisionEnum = null;

        if (type != null) {
            try {
                transactionType = Transaction.TransactionType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid transaction type. Use INCOME or EXPENSE"));
            }
        }

        if (division != null) {
            try {
                divisionEnum = Transaction.Division.valueOf(division.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid division. Use OFFICE or PERSONAL"));
            }
        }

        // Service handles both date-based and non-date-based filtering
        List<Transaction> transactions = transactionService.getTransactionsByFilters(
                startDate, endDate, transactionType, divisionEnum, category
        );

        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Transaction>> getTransactionById(@PathVariable String id) {
        Transaction transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(ApiResponse.success(transaction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Transaction>> updateTransaction(
            @PathVariable String id,
            @RequestBody Transaction transaction
    ) {
        Transaction updated = transactionService.updateTransaction(id, transaction);
        return ResponseEntity.ok(ApiResponse.success(updated, "Transaction updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable String id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Transaction deleted successfully"));
    }
}