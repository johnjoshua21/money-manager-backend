package com.moneymanager.controller;

import com.moneymanager.dto.ApiResponse;
import com.moneymanager.model.Account;
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
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    
    private final AccountService accountService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<Account>> createAccount(@RequestBody Account account) {
        Account created = accountService.createAccount(account);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(created, "Account created successfully"));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Account>>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(ApiResponse.success(accounts));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Account>> getAccountById(@PathVariable String id) {
        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(ApiResponse.success(account));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Account>> updateAccount(
            @PathVariable String id,
            @RequestBody Account account
    ) {
        Account updated = accountService.updateAccount(id, account);
        return ResponseEntity.ok(ApiResponse.success(updated, "Account updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@PathVariable String id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Account deleted successfully"));
    }
}
