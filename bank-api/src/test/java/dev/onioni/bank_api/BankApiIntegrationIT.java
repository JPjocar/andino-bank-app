package dev.onioni.bank_api;

import dev.onioni.bank_api.domain.Account;
import dev.onioni.bank_api.domain.Transaction;
import dev.onioni.bank_api.dto.CreateAccountRequest;
import dev.onioni.bank_api.dto.MoneyRequest;
import dev.onioni.bank_api.dto.TransferRequest;
import dev.onioni.bank_api.repository.AccountRepository;
import dev.onioni.bank_api.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Testcontainers(disabledWithoutDocker = true)
class BankApiIntegrationIT {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @LocalServerPort
    private int port;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    private RestClient restClient;

    @BeforeEach
    void cleanDatabase() {
        transactionRepository.deleteAllInBatch();
        accountRepository.deleteAllInBatch();
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void createsAccountsAndMovesMoneyThroughApi() {
        Account source = createAccount("Integration source", "100.00");
        Account destination = createAccount("Integration destination", "25.00");

        Account deposited = restClient.post()
                .uri("/api/v1/accounts/{id}/deposit", source.getId())
                .body(new MoneyRequest(new BigDecimal("10.00")))
                .retrieve()
                .requiredBody(Account.class);
        assertBalance("110.00", deposited);

        Account withdrawn = restClient.post()
                .uri("/api/v1/accounts/{id}/withdraw", source.getId())
                .body(new MoneyRequest(new BigDecimal("15.00")))
                .retrieve()
                .requiredBody(Account.class);
        assertBalance("95.00", withdrawn);

        Account transferred = restClient.post()
                .uri("/api/v1/transfers")
                .body(new TransferRequest(source.getId(), destination.getId(), new BigDecimal("20.00")))
                .retrieve()
                .requiredBody(Account.class);
        assertBalance("75.00", transferred);

        Account sourceFromApi = restClient.get()
                .uri("/api/v1/accounts/{id}", source.getId())
                .retrieve()
                .requiredBody(Account.class);
        Account destinationFromApi = restClient.get()
                .uri("/api/v1/accounts/{id}", destination.getId())
                .retrieve()
                .requiredBody(Account.class);
        assertBalance("75.00", sourceFromApi);
        assertBalance("45.00", destinationFromApi);

        Transaction[] sourceTransactions = restClient.get()
                .uri("/api/v1/accounts/{id}/transactions", source.getId())
                .retrieve()
                .requiredBody(Transaction[].class);
        Transaction[] destinationTransactions = restClient.get()
                .uri("/api/v1/accounts/{id}/transactions", destination.getId())
                .retrieve()
                .requiredBody(Transaction[].class);
        assertEquals(4, sourceTransactions.length);
        assertEquals(2, destinationTransactions.length);
    }

    @Test
    void rejectsWithdrawalWhenTheAccountHasInsufficientFunds() {
        Account account = createAccount("Integration overdraft", "10.00");

        HttpClientErrorException response = org.junit.jupiter.api.Assertions.assertThrows(
                HttpClientErrorException.class,
                () -> restClient.post()
                        .uri("/api/v1/accounts/{id}/withdraw", account.getId())
                        .body(new MoneyRequest(new BigDecimal("10.01")))
                        .retrieve()
                        .requiredBody(Account.class));

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getResponseBodyAsString());
        org.junit.jupiter.api.Assertions.assertTrue(response.getResponseBodyAsString().contains("Insufficient funds"));
    }

    private Account createAccount(String ownerName, String initialBalance) {
        return restClient.post()
                .uri("/api/v1/accounts")
                .body(new CreateAccountRequest(ownerName, new BigDecimal(initialBalance)))
                .retrieve()
                .requiredBody(Account.class);
    }

    private void assertBalance(String expected, Account account) {
        assertEquals(0, new BigDecimal(expected).compareTo(account.getBalance()));
    }
}
