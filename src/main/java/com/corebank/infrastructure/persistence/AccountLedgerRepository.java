package com.corebank.infrastructure.persistence;

import com.corebank.domain.account.AccountLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountLedgerRepository extends JpaRepository<AccountLedger, UUID> {
    Optional<AccountLedger> findByAccountId(UUID accountId);
}
