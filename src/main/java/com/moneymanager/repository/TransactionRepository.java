package com.moneymanager.repository;

import com.moneymanager.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    // Find transactions by date range
    List<Transaction> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find by type
    List<Transaction> findByType(Transaction.TransactionType type);

    // Find by division
    List<Transaction> findByDivision(Transaction.Division division);

    // Find by category
    List<Transaction> findByCategory(String category);

    // Complex query - find by multiple filters
    @Query("{ 'date': { $gte: ?0, $lte: ?1 }, 'type': ?2, 'division': ?3, 'category': ?4 }")
    List<Transaction> findByFilters(LocalDateTime startDate, LocalDateTime endDate,
                                    Transaction.TransactionType type,
                                    Transaction.Division division,
                                    String category);

    // Find by date range and type
    List<Transaction> findByDateBetweenAndType(LocalDateTime startDate, LocalDateTime endDate,
                                               Transaction.TransactionType type);

    // Find by date range and division
    List<Transaction> findByDateBetweenAndDivision(LocalDateTime startDate, LocalDateTime endDate,
                                                   Transaction.Division division);

    // Find by date range and category
    List<Transaction> findByDateBetweenAndCategory(LocalDateTime startDate, LocalDateTime endDate,
                                                   String category);

    // Simple filters without date range (added for better filtering)
    List<Transaction> findByTypeAndDivisionAndCategory(Transaction.TransactionType type,
                                                       Transaction.Division division,
                                                       String category);

    List<Transaction> findByTypeAndDivision(Transaction.TransactionType type,
                                            Transaction.Division division);

    List<Transaction> findByTypeAndCategory(Transaction.TransactionType type, String category);

    List<Transaction> findByDivisionAndCategory(Transaction.Division division, String category);
}