package dev.onioni.bank_api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotBlank String ownerName,
        @DecimalMin("0.00") BigDecimal initialBalance
) {
    public CreateAccountRequest {
        initialBalance = initialBalance == null ? BigDecimal.ZERO : initialBalance;
    }
}
