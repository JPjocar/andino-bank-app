package dev.onioni.bank_api.controller;

import dev.onioni.bank_api.domain.Account;
import dev.onioni.bank_api.domain.Transaction;
import dev.onioni.bank_api.dto.CreateAccountRequest;
import dev.onioni.bank_api.dto.MoneyRequest;
import dev.onioni.bank_api.dto.TransferRequest;
import dev.onioni.bank_api.service.BankService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class BankControllerTest {
    @Mock
    private BankService bankService;
    private BankController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new BankController(bankService);
    }

    @Test
    void delegatesAccountOperations() {
        UUID id = UUID.randomUUID();
        Account account = new Account("Ana", "AND-12345678", new BigDecimal("100.00"));
        MoneyRequest money = new MoneyRequest(BigDecimal.TEN);
        CreateAccountRequest create = new CreateAccountRequest("Ana", BigDecimal.ZERO);
        TransferRequest transfer = new TransferRequest(id, UUID.randomUUID(), BigDecimal.ONE);
        Transaction transaction = new Transaction(id, dev.onioni.bank_api.domain.TransactionType.DEPOSIT,
                BigDecimal.TEN, "Deposit");
        when(bankService.findAccounts()).thenReturn(List.of(account));
        when(bankService.findAccount(id)).thenReturn(account);
        when(bankService.createAccount(create)).thenReturn(account);
        when(bankService.deposit(id, money)).thenReturn(account);
        when(bankService.withdraw(id, money)).thenReturn(account);
        when(bankService.transfer(transfer)).thenReturn(account);
        when(bankService.findTransactions(id)).thenReturn(List.of(transaction));

        assertEquals(List.of(account), controller.accounts());
        assertEquals(account, controller.account(id));
        assertEquals(account, controller.createAccount(create));
        assertEquals(account, controller.deposit(id, money));
        assertEquals(account, controller.withdraw(id, money));
        assertEquals(account, controller.transfer(transfer));
        assertEquals(List.of(transaction), controller.transactions(id));
    }
}
