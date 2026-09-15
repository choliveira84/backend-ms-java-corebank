package com.corebank.application.command;

import com.corebank.domain.account.AccountLedger;
import com.corebank.domain.transaction.OutboxEvent;
import com.corebank.domain.transaction.TransactionHistory;
import com.corebank.infrastructure.persistence.AccountLedgerRepository;
import com.corebank.infrastructure.persistence.OutboxEventRepository;
import com.corebank.infrastructure.persistence.TransactionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthorizeTransactionUseCaseTest {

    private AccountLedgerRepository ledgerRepository;
    private TransactionHistoryRepository historyRepository;
    private OutboxEventRepository outboxRepository;
    private AuthorizeTransactionUseCase useCase;

    @BeforeEach
    void setUp() {
        ledgerRepository = mock(AccountLedgerRepository.class);
        historyRepository = mock(TransactionHistoryRepository.class);
        outboxRepository = mock(OutboxEventRepository.class);
        useCase = new AuthorizeTransactionUseCase(ledgerRepository, historyRepository, outboxRepository);
    }

    @Test
    void shouldAuthorizeTransactionWhenSufficientFunds() {
        UUID accountId = UUID.randomUUID();
        AccountLedger ledger = new AccountLedger(UUID.randomUUID(), accountId, new BigDecimal("100.00"), 0, LocalDateTime.now());
        when(ledgerRepository.findByAccountId(accountId)).thenReturn(Optional.of(ledger));

        var command = new AuthorizeTransactionUseCase.AuthorizeTransactionCommand(accountId, new BigDecimal("50.00"), "DEBIT");
        var result = useCase.execute(command);

        assertEquals("AUTHORIZED", result.status());
        assertEquals(new BigDecimal("50.00"), ledger.getBalance());
        verify(ledgerRepository).save(ledger);
        verify(historyRepository).save(any(TransactionHistory.class));
        verify(outboxRepository).save(any(OutboxEvent.class));
    }

    @Test
    void shouldThrowExceptionWhenInsufficientFunds() {
        UUID accountId = UUID.randomUUID();
        AccountLedger ledger = new AccountLedger(UUID.randomUUID(), accountId, new BigDecimal("10.00"), 0, LocalDateTime.now());
        when(ledgerRepository.findByAccountId(accountId)).thenReturn(Optional.of(ledger));

        var command = new AuthorizeTransactionUseCase.AuthorizeTransactionCommand(accountId, new BigDecimal("50.00"), "DEBIT");

        assertThrows(com.corebank.domain.exception.BusinessRuleViolationException.class, () -> useCase.execute(command));
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {
        UUID accountId = UUID.randomUUID();
        when(ledgerRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

        var command = new AuthorizeTransactionUseCase.AuthorizeTransactionCommand(accountId, new BigDecimal("50.00"), "DEBIT");

        assertThrows(com.corebank.domain.exception.ResourceNotFoundException.class, () -> useCase.execute(command));
    }
}
