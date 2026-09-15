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

    public BalanceProjectionUpdater(BalanceRedisRepository repository, ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @RabbitListener(queues = "transaction.authorized.queue")
    public void onTransactionAuthorized(String payload) {
        try {
            JsonNode node = mapper.readTree(payload);
            UUID accountId = UUID.fromString(node.get("accountId").asText());
            // Ideally the event should contain the new balance, but for this exercise we 
            // fetch it or we could subtract it. In CQRS, the read model projection could calculate it.
            // Here we'll just simulate an update.
            var existing = repository.getBalance(accountId).orElse(new BalanceProjection(accountId, BigDecimal.ZERO, LocalDateTime.now()));
            BigDecimal amount = new BigDecimal(node.get("amount").asText());
            
            BalanceProjection updated = new BalanceProjection(accountId, existing.availableBalance().subtract(amount), LocalDateTime.now());
            repository.saveBalance(updated);
            
            log.info("Updated balance projection for account {}", accountId);
        } catch (Exception e) {
            log.error("Error updating balance projection", e);
        }
    }
}
