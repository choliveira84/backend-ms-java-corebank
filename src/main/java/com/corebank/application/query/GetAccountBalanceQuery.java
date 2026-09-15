package com.corebank.application.query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface GetAccountBalanceQuery {

    Optional<Result> execute(UUID accountId);

    record Result(UUID accountId, BigDecimal availableBalance, LocalDateTime lastUpdatedAt) {
    }
}
