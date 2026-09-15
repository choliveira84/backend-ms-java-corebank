package com.corebank.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public interface AuthorizeTransactionUseCase {

    AuthorizeTransactionResult execute(AuthorizeTransactionCommand command);

    record AuthorizeTransactionCommand(UUID accountId, BigDecimal amount, String type) {}
    record AuthorizeTransactionResult(UUID transactionId, String status, String message) {}
}
