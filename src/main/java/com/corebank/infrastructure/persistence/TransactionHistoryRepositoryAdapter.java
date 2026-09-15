package com.corebank.infrastructure.persistence;

import com.corebank.domain.transaction.TransactionHistory;
import com.corebank.domain.transaction.TransactionHistoryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionHistoryRepositoryAdapter implements TransactionHistoryRepository {

    private final TransactionHistoryJpaRepository jpaRepository;

    public TransactionHistoryRepositoryAdapter(TransactionHistoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(TransactionHistory history) {
        jpaRepository.save(history);
    }
}
