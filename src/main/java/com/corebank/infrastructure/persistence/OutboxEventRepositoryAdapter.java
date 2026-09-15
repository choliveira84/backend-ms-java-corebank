package com.corebank.infrastructure.persistence;

import com.corebank.domain.transaction.OutboxEvent;
import com.corebank.domain.transaction.OutboxEventRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OutboxEventRepositoryAdapter implements OutboxEventRepository {

    private final OutboxEventJpaRepository jpaRepository;

    public OutboxEventRepositoryAdapter(OutboxEventJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<OutboxEvent> findByProcessedFalseOrderByCreatedAtAsc() {
        return jpaRepository.findByProcessedFalseOrderByCreatedAtAsc();
    }

    @Override
    public void save(OutboxEvent event) {
        jpaRepository.save(event);
    }
}
