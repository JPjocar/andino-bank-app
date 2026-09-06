package dev.onioni.bank_api.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransactionTest {
    @Test
    void exposesTransactionData() {
        UUID accountId = UUID.randomUUID();
        Transaction transaction = new Transaction(accountId, TransactionType.TRANSFER_IN,
                new BigDecimal("20.00"), "Transfer");

        assertNotNull(transaction.getId());
        assertEquals(accountId, transaction.getAccountId());
        assertEquals(TransactionType.TRANSFER_IN, transaction.getType());
        assertEquals(new BigDecimal("20.00"), transaction.getAmount());
        assertEquals("Transfer", transaction.getDescription());
        assertNotNull(transaction.getCreatedAt());
    }
}
