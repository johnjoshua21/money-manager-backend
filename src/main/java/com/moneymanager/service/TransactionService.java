package com.moneymanager.service;

import com.moneymanager.exception.ResourceNotFoundException;
import com.moneymanager.exception.EditTimeExpiredException;
import com.moneymanager.model.Transaction;
import com.moneymanager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private static final long EDIT_TIME_LIMIT_HOURS = 12;

    public Transaction createTransaction(Transaction transaction) {
        if (transaction.getDate() == null) {
            transaction.setDate(LocalDateTime.now());
        }
        Transaction saved = transactionRepository.save(transaction);
        saved.setIsEditable(isEditable(saved));
        return saved;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        transactions.forEach(t -> t.setIsEditable(isEditable(t)));
        return transactions;
    }

    public Transaction getTransactionById(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        transaction.setIsEditable(isEditable(transaction));
        return transaction;
    }

    public Transaction updateTransaction(String id, Transaction transactionDetails) {
        Transaction transaction = getTransactionById(id);

        // Check if transaction is still editable (within 12 hours)
        if (!isEditable(transaction)) {
            throw new EditTimeExpiredException("Transaction can only be edited within 12 hours of creation");
        }

        // Update fields
        if (transactionDetails.getAmount() != null) {
            transaction.setAmount(transactionDetails.getAmount());
        }
        if (transactionDetails.getCategory() != null) {
            transaction.setCategory(transactionDetails.getCategory());
        }
        if (transactionDetails.getDivision() != null) {
            transaction.setDivision(transactionDetails.getDivision());
        }
        if (transactionDetails.getDescription() != null) {
            transaction.setDescription(transactionDetails.getDescription());
        }
        if (transactionDetails.getDate() != null) {
            transaction.setDate(transactionDetails.getDate());
        }
        if (transactionDetails.getType() != null) {
            transaction.setType(transactionDetails.getType());
        }

        Transaction updated = transactionRepository.save(transaction);
        updated.setIsEditable(isEditable(updated));
        return updated;
    }

    public void deleteTransaction(String id) {
        Transaction transaction = getTransactionById(id);

        // Check if transaction is still editable (within 12 hours)
        if (!isEditable(transaction)) {
            throw new EditTimeExpiredException("Transaction can only be deleted within 12 hours of creation");
        }

        transactionRepository.deleteById(id);
    }

    public List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = transactionRepository.findByDateBetween(startDate, endDate);
        transactions.forEach(t -> t.setIsEditable(isEditable(t)));
        return transactions;
    }

    public List<Transaction> getTransactionsByFilters(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Transaction.TransactionType type,
            Transaction.Division division,
            String category) {

        List<Transaction> transactions;

        // If dates are provided, use date-based queries
        if (startDate != null && endDate != null) {
            // Apply filters based on what's provided
            if (type != null && division != null && category != null) {
                transactions = transactionRepository.findByFilters(startDate, endDate, type, division, category);
            } else if (type != null) {
                transactions = transactionRepository.findByDateBetweenAndType(startDate, endDate, type);
            } else if (division != null) {
                transactions = transactionRepository.findByDateBetweenAndDivision(startDate, endDate, division);
            } else if (category != null) {
                transactions = transactionRepository.findByDateBetweenAndCategory(startDate, endDate, category);
            } else {
                transactions = transactionRepository.findByDateBetween(startDate, endDate);
            }
        } else {
            // No dates provided, filter without date constraints
            if (type != null && division != null && category != null) {
                transactions = transactionRepository.findByTypeAndDivisionAndCategory(type, division, category);
            } else if (type != null && division != null) {
                transactions = transactionRepository.findByTypeAndDivision(type, division);
            } else if (type != null && category != null) {
                transactions = transactionRepository.findByTypeAndCategory(type, category);
            } else if (division != null && category != null) {
                transactions = transactionRepository.findByDivisionAndCategory(division, category);
            } else if (type != null) {
                transactions = transactionRepository.findByType(type);
            } else if (division != null) {
                transactions = transactionRepository.findByDivision(division);
            } else if (category != null) {
                transactions = transactionRepository.findByCategory(category);
            } else {
                transactions = transactionRepository.findAll();
            }
        }

        transactions.forEach(t -> t.setIsEditable(isEditable(t)));
        return transactions;
    }

    private boolean isEditable(Transaction transaction) {
        if (transaction.getCreatedAt() == null) {
            return true; // New transaction not yet saved
        }

        Duration duration = Duration.between(transaction.getCreatedAt(), LocalDateTime.now());
        return duration.toHours() < EDIT_TIME_LIMIT_HOURS;
    }
}