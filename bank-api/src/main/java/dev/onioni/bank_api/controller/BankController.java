package dev.onioni.bank_api.controller;

import dev.onioni.bank_api.domain.Account;
import dev.onioni.bank_api.domain.Transaction;
import dev.onioni.bank_api.dto.CreateAccountRequest;
import dev.onioni.bank_api.dto.MoneyRequest;
import dev.onioni.bank_api.dto.TransferRequest;
import dev.onioni.bank_api.service.BankService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "${app.frontend-url:http://localhost:4200}")
public class BankController {
    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping("/accounts")
    public List<Account> accounts() {
        return bankService.findAccounts();
    }

    @GetMapping("/accounts/{id}")
    public Account account(@PathVariable UUID id) {
        return bankService.findAccount(id);
    }

    @PostMapping("/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public Account createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return bankService.createAccount(request);
    }

    @PostMapping("/accounts/{id}/deposit")
    public Account deposit(@PathVariable UUID id, @Valid @RequestBody MoneyRequest request) {
        return bankService.deposit(id, request);
    }

    @PostMapping("/accounts/{id}/withdraw")
    public Account withdraw(@PathVariable UUID id, @Valid @RequestBody MoneyRequest request) {
        return bankService.withdraw(id, request);
    }

    @PostMapping("/transfers")
    public Account transfer(@Valid @RequestBody TransferRequest request) {
        return bankService.transfer(request);
    }

    @GetMapping("/accounts/{id}/transactions")
    public List<Transaction> transactions(@PathVariable UUID id) {
        return bankService.findTransactions(id);
    }
}
