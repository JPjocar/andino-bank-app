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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;
    private BankService bankService;

    @BeforeEach
    void setUp() {
        bankService = new BankService(accountRepository, transactionRepository);
    }

    @Test
    void findsAccountsAndTransactions() {
        Account account = account("100.00");
        Transaction transaction = new Transaction(account.getId(), TransactionType.DEPOSIT,
                new BigDecimal("10.00"), "Deposit");
        when(accountRepository.findAll()).thenReturn(List.of(account));
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountIdOrderByCreatedAtDesc(account.getId()))
                .thenReturn(List.of(transaction));

        assertEquals(List.of(account), bankService.findAccounts());
        assertEquals(account, bankService.findAccount(account.getId()));
        assertEquals(List.of(transaction), bankService.findTransactions(account.getId()));
    }

    @Test
    void rejectsUnknownAccount() {
        UUID id = UUID.randomUUID();
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bankService.findAccount(id));
    }

    @Test
    void createsAccountWithAndWithoutInitialTransaction() {
        Account funded = bankService.createAccount(new CreateAccountRequest("Ana", new BigDecimal("25.00")));
        Account empty = bankService.createAccount(new CreateAccountRequest("Luis", BigDecimal.ZERO));

        assertEquals(new BigDecimal("25.00"), funded.getBalance());
        assertEquals(BigDecimal.ZERO, empty.getBalance());
        verify(accountRepository, org.mockito.Mockito.times(2)).save(org.mockito.ArgumentMatchers.any(Account.class));
        verify(transactionRepository).save(org.mockito.ArgumentMatchers.any(Transaction.class));
    }

    @Test
    void depositsFunds() {
        Account account = account("100.00");
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        bankService.deposit(account.getId(), new MoneyRequest(new BigDecimal("15.50")));

        assertEquals(new BigDecimal("115.50"), account.getBalance());
        verify(transactionRepository).save(org.mockito.ArgumentMatchers.any(Transaction.class));
    }

    @Test
    void withdrawsFundsAndRejectsInsufficientFunds() {
        Account account = account("100.00");
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        bankService.withdraw(account.getId(), new MoneyRequest(new BigDecimal("35.50")));
        assertEquals(new BigDecimal("64.50"), account.getBalance());

        assertThrows(BusinessException.class,
                () -> bankService.withdraw(account.getId(), new MoneyRequest(new BigDecimal("100.01"))));
        assertEquals(new BigDecimal("64.50"), account.getBalance());
    }

    @Test
    void transfersMoneyAndRejectsInvalidTransfers() {
        Account source = account("100.00");
        Account destination = account("20.00");
        when(accountRepository.findById(source.getId())).thenReturn(Optional.of(source));
        when(accountRepository.findById(destination.getId())).thenReturn(Optional.of(destination));

        bankService.transfer(new TransferRequest(source.getId(), destination.getId(), new BigDecimal("35.50")));

        assertEquals(new BigDecimal("64.50"), source.getBalance());
        assertEquals(new BigDecimal("55.50"), destination.getBalance());
        verify(transactionRepository, org.mockito.Mockito.times(2))
                .save(org.mockito.ArgumentMatchers.any(Transaction.class));
        assertThrows(BusinessException.class, () -> bankService.transfer(
                new TransferRequest(source.getId(), source.getId(), BigDecimal.ONE)));
    }

    private Account account(String balance) {
        return new Account("Test user", "AND-" + UUID.randomUUID().toString().substring(0, 8),
                new BigDecimal(balance));
    }
}
