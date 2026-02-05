package com.moneymanager.service;

import com.moneymanager.exception.InsufficientBalanceException;
import com.moneymanager.exception.ResourceNotFoundException;
import com.moneymanager.model.Account;
import com.moneymanager.model.Transfer;
import com.moneymanager.repository.AccountRepository;
import com.moneymanager.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    
    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;
    
    public Account createAccount(Account account) {
        if (account.getBalance() == null) {
            account.setBalance(0.0);
        }
        return accountRepository.save(account);
    }
    
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    
    public Account getAccountById(String id) {
        return accountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
    }
    
    public Account updateAccount(String id, Account accountDetails) {
        Account account = getAccountById(id);
        
        if (accountDetails.getAccountName() != null) {
            account.setAccountName(accountDetails.getAccountName());
        }
        if (accountDetails.getBalance() != null) {
            account.setBalance(accountDetails.getBalance());
        }
        
        return accountRepository.save(account);
    }
    
    public void deleteAccount(String id) {
        Account account = getAccountById(id);
        accountRepository.delete(account);
    }
    
    @Transactional
    public Transfer createTransfer(Transfer transfer) {
        // Validate accounts exist
        Account fromAccount = getAccountById(transfer.getFromAccountId());
        Account toAccount = getAccountById(transfer.getToAccountId());
        
        // Check sufficient balance
        if (fromAccount.getBalance() < transfer.getAmount()) {
            throw new InsufficientBalanceException(
                "Insufficient balance in account: " + fromAccount.getAccountName()
            );
        }
        
        // Perform transfer
        fromAccount.setBalance(fromAccount.getBalance() - transfer.getAmount());
        toAccount.setBalance(toAccount.getBalance() + transfer.getAmount());
        
        // Save updated accounts
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        
        // Save transfer record
        if (transfer.getDate() == null) {
            transfer.setDate(LocalDateTime.now());
        }
        return transferRepository.save(transfer);
    }
    
    public List<Transfer> getAllTransfers() {
        return transferRepository.findAll();
    }
    
    public List<Transfer> getTransfersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transferRepository.findByDateBetween(startDate, endDate);
    }
    
    public Transfer getTransferById(String id) {
        return transferRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transfer not found with id: " + id));
    }
}
