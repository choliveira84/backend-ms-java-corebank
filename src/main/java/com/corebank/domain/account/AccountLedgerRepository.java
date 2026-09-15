package com.corebank.domain.account;

import java.util.Optional;
import java.util.UUID;

public interface AccountLedgerRepository {
    Optional<AccountLedger> findByAccountId(UUID accountId);
    void save(AccountLedger ledger);
}
