package com.corebank.application.query;

import com.corebank.domain.account.BalanceProjection;

import java.util.Optional;
import java.util.UUID;

public interface GetBalanceQuery {
    Optional<BalanceProjection> execute(UUID accountId);
}
