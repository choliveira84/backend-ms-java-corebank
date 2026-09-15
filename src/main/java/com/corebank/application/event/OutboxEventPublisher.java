package com.corebank.application.event;

import com.corebank.infrastructure.config.RabbitMQConfig;
import com.corebank.domain.transaction.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxEventPublisher.class);
    private final OutboxEventRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;

    public OutboxEventPublisher(OutboxEventRepository outboxRepository, RabbitTemplate rabbitTemplate) {
        this.outboxRepository = outboxRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void processOutbox() {
        var events = outboxRepository.findByProcessedFalseOrderByCreatedAtAsc();
        for (var event : events) {
            try {
                // In a real app we'd convert JSON string to object or use a proper Message payload
                rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "transaction.authorized", event.getPayload());
                event.setProcessed(true);
                outboxRepository.save(event);
                log.info("Published event {}", event.getId());
            } catch (Exception e) {
                log.error("Failed to publish event {}", event.getId(), e);
            }
        }
    }
}
