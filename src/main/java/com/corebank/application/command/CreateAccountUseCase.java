package com.corebank.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public interface CreateAccountUseCase {

    CreateAccountResult execute(CreateAccountCommand command);

    record CreateAccountCommand(UUID accountId, BigDecimal initialBalance) {}
    record CreateAccountResult(UUID accountId, BigDecimal balance, String status) {}
}
