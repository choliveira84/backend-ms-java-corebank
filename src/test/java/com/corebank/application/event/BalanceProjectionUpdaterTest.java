package com.corebank.application.event;

import com.corebank.domain.account.BalanceProjection;
import com.corebank.infrastructure.persistence.BalanceRedisRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BalanceProjectionUpdaterTest {

    @Test
    void shouldSubtractAuthorizedAmountFromExistingProjection() {
        UUID accountId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        LocalDateTime eventTime = LocalDateTime.ofInstant(Instant.parse("2026-09-15T12:00:00Z"), ZoneOffset.UTC);
        BalanceRedisRepository repository = mock(BalanceRedisRepository.class);
        ProjectionConsistencyPolicy policy = new ProjectionConsistencyPolicy(
                java.time.Duration.ofSeconds(5), Clock.fixed(Instant.parse("2026-09-15T12:00:01Z"), ZoneOffset.UTC));
        BalanceProjection existing = new BalanceProjection(accountId, new BigDecimal("100.00"), eventTime);
        when(repository.getBalance(accountId)).thenReturn(Optional.of(existing));
        BalanceProjectionUpdater updater = new BalanceProjectionUpdater(repository, new ObjectMapper(), policy);

        updater.onTransactionAuthorized("{\"eventId\":\"event-1\",\"transactionId\":\"" + transactionId
                + "\",\"accountId\":\"" + accountId + "\",\"amount\":25.00,\"status\":\"AUTHORIZED\",\"timestamp\":\""
                + eventTime + "\"}");

        verify(repository).saveBalance(any(BalanceProjection.class));
        org.mockito.ArgumentCaptor<BalanceProjection> captor = org.mockito.ArgumentCaptor.forClass(BalanceProjection.class);
        verify(repository).saveBalance(captor.capture());
        org.junit.jupiter.api.Assertions.assertEquals(new BigDecimal("75.00"), captor.getValue().availableBalance());
        org.junit.jupiter.api.Assertions.assertEquals(eventTime, captor.getValue().lastUpdatedAt());
    }

        @Test
        void shouldIgnoreDuplicateAuthorizationEvent() {
        UUID accountId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        BalanceRedisRepository repository = mock(BalanceRedisRepository.class);
        BalanceProjection existing = new BalanceProjection(accountId, new BigDecimal("75.00"),
            LocalDateTime.of(2026, 9, 15, 12, 0), transactionId);
        when(repository.getBalance(accountId)).thenReturn(Optional.of(existing));
        BalanceProjectionUpdater updater = new BalanceProjectionUpdater(repository, new ObjectMapper(),
            new ProjectionConsistencyPolicy(java.time.Duration.ofSeconds(5), Clock.systemUTC()));

        updater.onTransactionAuthorized("{\"transactionId\":\"" + transactionId + "\",\"accountId\":\""
            + accountId + "\",\"amount\":25.00,\"timestamp\":\"2026-09-15T12:00:00\"}");

        verify(repository, never()).saveBalance(any(BalanceProjection.class));
        }

        @Test
        void shouldApplySequentialAuthorizationEvents() {
        UUID accountId = UUID.randomUUID();
        UUID firstTransaction = UUID.randomUUID();
        UUID secondTransaction = UUID.randomUUID();
        BalanceRedisRepository repository = mock(BalanceRedisRepository.class);
        when(repository.getBalance(accountId))
            .thenReturn(Optional.of(new BalanceProjection(accountId, new BigDecimal("100.00"),
                LocalDateTime.of(2026, 9, 15, 12, 0))))
            .thenReturn(Optional.of(new BalanceProjection(accountId, new BigDecimal("75.00"),
                LocalDateTime.of(2026, 9, 15, 12, 0), firstTransaction)));
        BalanceProjectionUpdater updater = new BalanceProjectionUpdater(repository, new ObjectMapper(),
            new ProjectionConsistencyPolicy(java.time.Duration.ofSeconds(5), Clock.systemUTC()));

        updater.onTransactionAuthorized("{\"transactionId\":\"" + firstTransaction + "\",\"accountId\":\""
            + accountId + "\",\"amount\":25.00,\"timestamp\":\"2026-09-15T12:00:00\"}");
        updater.onTransactionAuthorized("{\"transactionId\":\"" + secondTransaction + "\",\"accountId\":\""
            + accountId + "\",\"amount\":10.00,\"timestamp\":\"2026-09-15T12:00:01\"}");

        org.mockito.ArgumentCaptor<BalanceProjection> captor = org.mockito.ArgumentCaptor.forClass(BalanceProjection.class);
        verify(repository, times(2)).saveBalance(captor.capture());
        org.junit.jupiter.api.Assertions.assertEquals(new BigDecimal("65.00"),
            captor.getAllValues().get(1).availableBalance());
        }
}
