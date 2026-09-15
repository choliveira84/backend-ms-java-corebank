package com.corebank.infrastructure.persistence;

import com.corebank.domain.account.AccountLedger;
import com.corebank.domain.account.AccountLedgerRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class AccountLedgerRepositoryAdapter implements AccountLedgerRepository {

    private final AccountLedgerJpaRepository jpaRepository;

    public AccountLedgerRepositoryAdapter(AccountLedgerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<AccountLedger> findByAccountId(UUID accountId) {
        return jpaRepository.findByAccountId(accountId);
    }

    @Override
    public void save(AccountLedger ledger) {
        jpaRepository.save(ledger);
    }
}
