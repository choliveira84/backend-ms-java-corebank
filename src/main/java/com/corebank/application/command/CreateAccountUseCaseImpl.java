package com.corebank.application.command;

import static java.util.UUID.randomUUID;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corebank.domain.account.AccountLedger;
import com.corebank.domain.account.AccountLedgerRepository;
import com.corebank.domain.account.BalanceProjection;
import com.corebank.domain.account.BalanceRepository;
import com.corebank.domain.exception.BusinessRuleViolationException;

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
        if (ledgerRepository.findByAccountId(command.accountId()).isPresent()) {
            throw new BusinessRuleViolationException("Account already exists");
        }

        AccountLedger accountLedger = new AccountLedger(
                randomUUID(), 
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
