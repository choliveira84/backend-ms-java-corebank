package com.corebank.application.command;

import com.corebank.domain.account.AccountLedger;
import com.corebank.domain.transaction.OutboxEvent;
import com.corebank.domain.transaction.TransactionHistory;
import com.corebank.domain.account.AccountLedgerRepository;
import com.corebank.domain.transaction.OutboxEventRepository;
import com.corebank.domain.transaction.TransactionHistoryRepository;
import com.corebank.domain.transaction.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

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
        useCase = new AuthorizeTransactionUseCaseImpl(ledgerRepository, historyRepository, outboxRepository);
    }

    @Test
    void shouldAuthorizeTransactionWhenSufficientFunds() {
        UUID accountId = UUID.randomUUID();
        AccountLedger ledger = new AccountLedger(UUID.randomUUID(), accountId, new BigDecimal("100.00"), 0, LocalDateTime.now());
        when(ledgerRepository.findByAccountId(accountId)).thenReturn(Optional.of(ledger));

        var command = new AuthorizeTransactionUseCase.AuthorizeTransactionCommand(accountId, new BigDecimal("50.00"), TransactionType.DEBIT);
        var result = useCase.execute(command);

        assertEquals("AUTHORIZED", result.status());
        assertEquals(new BigDecimal("50.00"), ledger.getBalance());
        verify(ledgerRepository).save(ledger);
        verify(historyRepository).save(any(TransactionHistory.class));
        verify(outboxRepository).save(any(OutboxEvent.class));
    }

    @Test
    void shouldPreservePixTypeInHistoryAndOutboxPayload() {
        UUID accountId = UUID.randomUUID();
        AccountLedger ledger = new AccountLedger(UUID.randomUUID(), accountId, new BigDecimal("100.00"), 0, LocalDateTime.now());
        when(ledgerRepository.findByAccountId(accountId)).thenReturn(Optional.of(ledger));

        var command = new AuthorizeTransactionUseCase.AuthorizeTransactionCommand(
                accountId, new BigDecimal("25.00"), TransactionType.PIX);
        useCase.execute(command);

        ArgumentCaptor<TransactionHistory> historyCaptor = ArgumentCaptor.forClass(TransactionHistory.class);
        verify(historyRepository).save(historyCaptor.capture());
        assertEquals("PIX", historyCaptor.getValue().getType());

        ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxRepository).save(outboxCaptor.capture());
        assertTrue(outboxCaptor.getValue().getPayload().contains("\"type\":\"PIX\""));
    }

    @Test
    void shouldThrowExceptionWhenInsufficientFunds() {
        UUID accountId = UUID.randomUUID();
        AccountLedger ledger = new AccountLedger(UUID.randomUUID(), accountId, new BigDecimal("10.00"), 0, LocalDateTime.now());
        when(ledgerRepository.findByAccountId(accountId)).thenReturn(Optional.of(ledger));

        var command = new AuthorizeTransactionUseCase.AuthorizeTransactionCommand(accountId, new BigDecimal("50.00"), TransactionType.DEBIT);

        assertThrows(com.corebank.domain.exception.BusinessRuleViolationException.class, () -> useCase.execute(command));
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {
        UUID accountId = UUID.randomUUID();
        when(ledgerRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

        var command = new AuthorizeTransactionUseCase.AuthorizeTransactionCommand(accountId, new BigDecimal("50.00"), TransactionType.DEBIT);

        assertThrows(com.corebank.domain.exception.ResourceNotFoundException.class, () -> useCase.execute(command));
    }
}
