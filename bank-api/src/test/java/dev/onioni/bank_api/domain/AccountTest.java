package dev.onioni.bank_api.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AccountTest {
    @Test
    void exposesAccountDataAndChangesBalance() {
        Account account = new Account("Ana", "AND-12345678", new BigDecimal("100.00"));

        assertNotNull(account.getId());
        assertEquals("Ana", account.getOwnerName());
        assertEquals("AND-12345678", account.getAccountNumber());
        assertEquals("USD", account.getCurrency());
        assertNotNull(account.getCreatedAt());
        account.deposit(new BigDecimal("10.00"));
        account.withdraw(new BigDecimal("5.00"));
        assertEquals(new BigDecimal("105.00"), account.getBalance());
    }
}
