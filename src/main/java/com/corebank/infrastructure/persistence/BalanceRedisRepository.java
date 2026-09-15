package com.corebank.infrastructure.persistence;

import com.corebank.domain.account.BalanceProjection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

        if (amountObj == null) {
            return Optional.empty();
        }

        BigDecimal amount = new BigDecimal(amountObj.toString());
        LocalDateTime date = dateObj != null ? LocalDateTime.parse(dateObj.toString()) : LocalDateTime.now();

        return Optional.of(new BalanceProjection(accountId, amount, date));
    }

    public void saveBalance(BalanceProjection projection) {
        String key = "account:" + projection.accountId() + ":balance";
        redisTemplate.opsForHash().put(key, "availableAmount", projection.availableBalance().toString());
        redisTemplate.opsForHash().put(key, "lastUpdatedAt", projection.lastUpdatedAt().toString());
    }
}
