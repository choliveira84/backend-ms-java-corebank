package com.corebank.application.command;

import java.math.BigDecimal;
import java.util.UUID;

import com.corebank.domain.transaction.TransactionType;

public interface AuthorizeTransactionUseCase {

    AuthorizeTransactionResult execute(AuthorizeTransactionCommand command);

    record AuthorizeTransactionCommand(UUID accountId, BigDecimal amount, TransactionType type) {}
    record AuthorizeTransactionResult(UUID transactionId, String status, String message) {}
}
