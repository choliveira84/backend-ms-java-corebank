package com.corebank.domain.account;

import java.util.Optional;
import java.util.UUID;

public interface BalanceRepository {
    Optional<BalanceProjection> getBalance(UUID accountId);
    void saveBalance(BalanceProjection projection);
}
