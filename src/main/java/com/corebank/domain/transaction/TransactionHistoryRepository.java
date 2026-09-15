package com.corebank.domain.transaction;

public interface TransactionHistoryRepository {
    void save(TransactionHistory history);
}
