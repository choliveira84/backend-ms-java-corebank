package com.corebank.infrastructure.persistence;

import com.corebank.domain.transaction.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface TransactionHistoryJpaRepository extends JpaRepository<TransactionHistory, UUID> {
}
