package com.corebank.application.command;

import com.corebank.domain.account.AccountLedger;
import com.corebank.domain.account.AccountLedgerRepository;
import com.corebank.domain.account.BalanceProjection;
import com.corebank.domain.account.BalanceRepository;
import com.corebank.domain.exception.BusinessRuleViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CreateAccountUseCaseImpl implements CreateAccountUseCase {

    private final AccountLedgerRepository ledgerRepository;
    private final BalanceRepository balanceRepository;

    public CreateAccountUseCaseImpl(AccountLedgerRepository ledgerRepository, BalanceRepository balanceRepository) {
        this.ledgerRepository = ledgerRepository;
        this.balanceRepository = balanceRepository;
    }

    @Override
    @Transactional
    public CreateAccountResult execute(CreateAccountCommand command) {
        if (command.initialBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleViolationException("Initial balance cannot be negative");
        }

        if (ledgerRepository.findByAccountId(command.accountId()).isPresent()) {
            throw new BusinessRuleViolationException("Account already exists");
        }

        AccountLedger accountLedger = new AccountLedger(
                java.util.UUID.randomUUID(), 
                command.accountId(), 
                command.initialBalance(), 
                0, 
                LocalDateTime.now()
        );
        ledgerRepository.save(accountLedger);

        BalanceProjection balanceProjection = new BalanceProjection(
                command.accountId(),
                command.initialBalance(),
                LocalDateTime.now()
        );
        balanceRepository.saveBalance(balanceProjection);

        return new CreateAccountResult(command.accountId(), command.initialBalance(), "CREATED");
    }
}
