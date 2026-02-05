package com.moneymanager.controller;

import com.moneymanager.dto.ApiResponse;
import com.moneymanager.model.Transfer;
import com.moneymanager.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {
    
    private final AccountService accountService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<Transfer>> createTransfer(@RequestBody Transfer transfer) {
        Transfer created = accountService.createTransfer(transfer);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(created, "Transfer completed successfully"));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Transfer>>> getAllTransfers(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        List<Transfer> transfers;
        
        if (startDate != null && endDate != null) {
            transfers = accountService.getTransfersByDateRange(startDate, endDate);
        } else {
            transfers = accountService.getAllTransfers();
        }
        
        return ResponseEntity.ok(ApiResponse.success(transfers));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Transfer>> getTransferById(@PathVariable String id) {
        Transfer transfer = accountService.getTransferById(id);
        return ResponseEntity.ok(ApiResponse.success(transfer));
    }
}
