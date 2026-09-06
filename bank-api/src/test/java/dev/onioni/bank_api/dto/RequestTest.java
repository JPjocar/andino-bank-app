package dev.onioni.bank_api.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestTest {
    @Test
    void defaultsMissingInitialBalanceToZero() {
        assertEquals(BigDecimal.ZERO, new CreateAccountRequest("Ana", null).initialBalance());
        assertEquals(new BigDecimal("10.00"),
                new CreateAccountRequest("Ana", new BigDecimal("10.00")).initialBalance());
    }
}
