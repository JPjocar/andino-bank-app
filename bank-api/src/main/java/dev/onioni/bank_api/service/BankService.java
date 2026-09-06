package dev.onioni.bank_api.service;

import dev.onioni.bank_api.domain.Account;
import dev.onioni.bank_api.domain.Transaction;
import dev.onioni.bank_api.domain.TransactionType;
import dev.onioni.bank_api.dto.CreateAccountRequest;
import dev.onioni.bank_api.dto.MoneyRequest;
import dev.onioni.bank_api.dto.TransferRequest;
import dev.onioni.bank_api.exception.BusinessException;
import dev.onioni.bank_api.exception.ResourceNotFoundException;
import dev.onioni.bank_api.repository.AccountRepository;
import dev.onioni.bank_api.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class BankService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public BankService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<Account> findAccounts() {
        return accountRepository.findAll();
    }

    public Account findAccount(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

    @Transactional
    public Account createAccount(CreateAccountRequest request) {
        Account account = new Account(request.ownerName(), generateAccountNumber(), request.initialBalance());
        accountRepository.save(account);
        if (request.initialBalance().signum() > 0) {
            transactionRepository.save(new Transaction(account.getId(), TransactionType.DEPOSIT,
                    request.initialBalance(), "Initial balance"));
        }
        return account;
    }

    @Transactional
    public Account deposit(UUID id, MoneyRequest request) {
        Account account = findAccount(id);
        account.deposit(request.amount());
        transactionRepository.save(new Transaction(id, TransactionType.DEPOSIT, request.amount(), "Cash deposit"));
        return account;
    }

    @Transactional
    public Account withdraw(UUID id, MoneyRequest request) {
        Account account = findAccount(id);
        ensureFunds(account, request.amount());
        account.withdraw(request.amount());
        transactionRepository.save(new Transaction(id, TransactionType.WITHDRAWAL, request.amount(), "Cash withdrawal"));
        return account;
    }

    @Transactional
    public Account transfer(TransferRequest request) {
        if (request.fromAccountId().equals(request.toAccountId())) {
            throw new BusinessException("Source and destination accounts must be different");
        }
        Account source = findAccount(request.fromAccountId());
        Account destination = findAccount(request.toAccountId());
        ensureFunds(source, request.amount());
        source.withdraw(request.amount());
        destination.deposit(request.amount());
        transactionRepository.save(new Transaction(source.getId(), TransactionType.TRANSFER_OUT,
                request.amount(), "Transfer to " + destination.getAccountNumber()));
        transactionRepository.save(new Transaction(destination.getId(), TransactionType.TRANSFER_IN,
                request.amount(), "Transfer from " + source.getAccountNumber()));
        return source;
    }

    public List<Transaction> findTransactions(UUID accountId) {
        findAccount(accountId);
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    private void ensureFunds(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("Insufficient funds");
        }
    }

    private String generateAccountNumber() {
        return "AND-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }
}
