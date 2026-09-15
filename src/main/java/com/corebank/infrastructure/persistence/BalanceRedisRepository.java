package com.corebank.infrastructure.persistence;

import com.corebank.domain.account.BalanceProjection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import com.corebank.domain.account.BalanceRepository;

@Repository
public class BalanceRedisRepository implements BalanceRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public BalanceRedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Optional<BalanceProjection> getBalance(UUID accountId) {
        String key = "account:" + accountId + ":balance";
        Object amountObj = redisTemplate.opsForHash().get(key, "availableAmount");
        Object dateObj = redisTemplate.opsForHash().get(key, "lastUpdatedAt");
        Object transactionObj = redisTemplate.opsForHash().get(key, "lastAppliedTransactionId");

        if (amountObj == null) {
            return Optional.empty();
        }

        BigDecimal amount = new BigDecimal(amountObj.toString());
        LocalDateTime date = dateObj != null ? LocalDateTime.parse(dateObj.toString()) : LocalDateTime.now(ZoneOffset.UTC);
        UUID transactionId = transactionObj != null ? UUID.fromString(transactionObj.toString()) : null;

        return Optional.of(new BalanceProjection(accountId, amount, date, transactionId));
    }

    public void saveBalance(BalanceProjection projection) {
        String key = "account:" + projection.accountId() + ":balance";
        redisTemplate.opsForHash().put(key, (Object) "availableAmount",
            (Object) projection.availableBalance().toString());
        redisTemplate.opsForHash().put(key, (Object) "lastUpdatedAt",
            (Object) projection.lastUpdatedAt().toString());
        if (projection.lastAppliedTransactionId() != null) {
            redisTemplate.opsForHash().put(key, (Object) "lastAppliedTransactionId",
                (Object) projection.lastAppliedTransactionId().toString());
        }
    }
}
