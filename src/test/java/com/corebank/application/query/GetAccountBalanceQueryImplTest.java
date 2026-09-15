package com.corebank.application.query;

import com.corebank.domain.account.BalanceProjection;
import com.corebank.domain.account.BalanceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAccountBalanceQueryImplTest {

    @Mock
    private BalanceRepository balanceRepository;

    @InjectMocks
    private GetAccountBalanceQueryImpl getAccountBalanceQuery;

    @Test
    @DisplayName("Should return account balance successfully")
    void shouldReturnAccountBalance() {
        // Given
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("500.00");
        LocalDateTime date = LocalDateTime.now();
        BalanceProjection projection = new BalanceProjection(accountId, amount, date);

        when(balanceRepository.getBalance(accountId)).thenReturn(Optional.of(projection));

        // When
        Optional<GetAccountBalanceQuery.Result> result = getAccountBalanceQuery.execute(accountId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(accountId, result.get().accountId());
        assertEquals(amount, result.get().availableBalance());
        assertEquals(date, result.get().lastUpdatedAt());
        verify(balanceRepository).getBalance(accountId);
    }

    @Test
    @DisplayName("Should return empty when account is not found")
    void shouldReturnEmptyWhenAccountNotFound() {
        // Given
        UUID accountId = UUID.randomUUID();
        when(balanceRepository.getBalance(accountId)).thenReturn(Optional.empty());

        // When
        Optional<GetAccountBalanceQuery.Result> result = getAccountBalanceQuery.execute(accountId);

        // Then
        assertTrue(result.isEmpty());
        verify(balanceRepository).getBalance(accountId);
    }
}
