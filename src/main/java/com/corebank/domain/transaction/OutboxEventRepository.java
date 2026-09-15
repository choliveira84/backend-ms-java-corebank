package com.corebank.domain.transaction;

import java.util.List;

public interface OutboxEventRepository {
    List<OutboxEvent> findByProcessedFalseOrderByCreatedAtAsc();
    void save(OutboxEvent event);
}
