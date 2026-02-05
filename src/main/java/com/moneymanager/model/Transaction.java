package com.moneymanager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class Transaction {
    
    @Id
    private String id;
    
    private TransactionType type; // INCOME or EXPENSE
    
    private Double amount;
    
    private String category;
    
    private Division division; // OFFICE or PERSONAL
    
    private String description;
    
    private LocalDateTime date;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Computed field - not stored in DB
    private Boolean isEditable;
    
    public enum TransactionType {
        INCOME,
        EXPENSE
    }
    
    public enum Division {
        OFFICE,
        PERSONAL
    }
}
