package com.corebank.domain.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BalanceProjection(
        UUID accountId,
        BigDecimal availableBalance,
                LocalDateTime lastUpdatedAt,
                UUID lastAppliedTransactionId
) {

        public BalanceProjection(UUID accountId, BigDecimal availableBalance, LocalDateTime lastUpdatedAt) {
                this(accountId, availableBalance, lastUpdatedAt, null);
        }
}
