package com.corebank.application.event;

import com.corebank.domain.account.BalanceProjection;
import com.corebank.infrastructure.persistence.BalanceRedisRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class BalanceProjectionUpdater {

    private static final Logger log = LoggerFactory.getLogger(BalanceProjectionUpdater.class);
    private final BalanceRedisRepository repository;
    private final ObjectMapper mapper;
    private final ProjectionConsistencyPolicy consistencyPolicy;

    public BalanceProjectionUpdater(BalanceRedisRepository repository, ObjectMapper mapper,
            ProjectionConsistencyPolicy consistencyPolicy) {
        this.repository = repository;
        this.mapper = mapper;
        this.consistencyPolicy = consistencyPolicy;
    }

    @RabbitListener(queues = "transaction.authorized.queue")
    public void onTransactionAuthorized(String payload) {
        try {
            JsonNode node = mapper.readTree(payload);
            UUID accountId = UUID.fromString(node.get("accountId").asText());
                var existing = repository.getBalance(accountId);
                if (existing.isEmpty()) {
                    log.warn("Projection skipped because no initial balance exists for account {}", accountId);
                    return;
                }

                UUID transactionId = UUID.fromString(node.get("transactionId").asText());
                if (transactionId.equals(existing.get().lastAppliedTransactionId())) {
                    log.info("Duplicate transaction event ignored for account {} and transaction {}", accountId, transactionId);
                    return;
                }

            BigDecimal amount = new BigDecimal(node.get("amount").asText());
                LocalDateTime eventTime = node.hasNonNull("timestamp")
                    ? LocalDateTime.parse(node.get("timestamp").asText())
                    : LocalDateTime.now(consistencyPolicy.clock());
                ProjectionFreshness freshness = consistencyPolicy.freshnessOf(eventTime);
                if (freshness.stale()) {
                    log.warn("Stale transaction event detected for account {} with age {} and threshold {}",
                            accountId, freshness.age(), freshness.maxDelay());
                }

                BalanceProjection updated = new BalanceProjection(accountId,
                        existing.get().availableBalance().subtract(amount), eventTime, transactionId);
            repository.saveBalance(updated);
            
            log.info("Updated balance projection for account {}", accountId);
        } catch (Exception e) {
            log.error("Error updating balance projection", e);
        }
    }
}
