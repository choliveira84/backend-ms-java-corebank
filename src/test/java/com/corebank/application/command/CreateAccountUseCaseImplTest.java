package com.corebank.application.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.corebank.domain.account.AccountLedger;
import com.corebank.domain.account.AccountLedgerRepository;
import com.corebank.domain.account.BalanceProjection;
import com.corebank.domain.account.BalanceRepository;
import com.corebank.domain.exception.BusinessRuleViolationException;

class CreateAccountUseCaseImplTest {

    private AccountLedgerRepository ledgerRepository;
    private BalanceRepository balanceRepository;
    private CreateAccountUseCase useCase;

    @BeforeEach
    void setUp() {
        ledgerRepository = mock(AccountLedgerRepository.class);
        balanceRepository = mock(BalanceRepository.class);
        useCase = new CreateAccountUseCaseImpl(ledgerRepository, balanceRepository);
    }

    @Test
    void execute_ShouldCreateAccountAndSaveToBothRepositories() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        BigDecimal balance = new BigDecimal("5000.00");
        CreateAccountUseCase.CreateAccountCommand command = new CreateAccountUseCase.CreateAccountCommand(accountId, balance);

        when(ledgerRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

        // Act
        CreateAccountUseCase.CreateAccountResult result = useCase.execute(command);

        // Assert
        assertEquals(accountId, result.accountId());
        assertEquals(balance, result.balance());
        assertEquals("CREATED", result.status());

        ArgumentCaptor<AccountLedger> ledgerCaptor = ArgumentCaptor.forClass(AccountLedger.class);
        verify(ledgerRepository).save(ledgerCaptor.capture());
        AccountLedger savedLedger = ledgerCaptor.getValue();
        assertEquals(accountId, savedLedger.getAccountId());
        assertEquals(balance, savedLedger.getBalance());
        assertEquals(0, savedLedger.getVersion());

        ArgumentCaptor<BalanceProjection> balanceCaptor = ArgumentCaptor.forClass(BalanceProjection.class);
        verify(balanceRepository).saveBalance(balanceCaptor.capture());
        BalanceProjection savedBalance = balanceCaptor.getValue();
        assertEquals(accountId, savedBalance.accountId());
        assertEquals(balance, savedBalance.availableBalance());
    }

    @Test
    void execute_ShouldThrowException_WhenAccountAlreadyExists() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        BigDecimal balance = new BigDecimal("5000.00");
        CreateAccountUseCase.CreateAccountCommand command = new CreateAccountUseCase.CreateAccountCommand(accountId, balance);

        AccountLedger existingAccount = new AccountLedger(UUID.randomUUID(), accountId, BigDecimal.ZERO, 0, java.time.LocalDateTime.now());
        when(ledgerRepository.findByAccountId(accountId)).thenReturn(Optional.of(existingAccount));

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class, () -> useCase.execute(command));
        assertEquals("Account already exists", exception.getMessage());
        
        verify(ledgerRepository, never()).save(any());
        verify(balanceRepository, never()).saveBalance(any());
    }
}
