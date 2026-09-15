package com.corebank.application.command;

import static java.util.UUID.randomUUID;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corebank.domain.account.AccountLedger;
import com.corebank.domain.exception.BusinessRuleViolationException;
import com.corebank.domain.exception.ResourceNotFoundException;
import com.corebank.domain.transaction.OutboxEvent;
import com.corebank.domain.transaction.TransactionHistory;
import com.corebank.domain.account.AccountLedgerRepository;
import com.corebank.domain.transaction.OutboxEventRepository;
import com.corebank.domain.transaction.TransactionHistoryRepository;

@Service
public class AuthorizeTransactionUseCaseImpl implements AuthorizeTransactionUseCase {

    private final AccountLedgerRepository ledgerRepository;
    private final TransactionHistoryRepository historyRepository;
    private final OutboxEventRepository outboxRepository;

    public AuthorizeTransactionUseCaseImpl(AccountLedgerRepository ledgerRepository,
                                       TransactionHistoryRepository historyRepository,
                                       OutboxEventRepository outboxRepository) {
        this.ledgerRepository = ledgerRepository;
        this.historyRepository = historyRepository;
        this.outboxRepository = outboxRepository;
    }

    @Override
    @Transactional
    public AuthorizeTransactionUseCase.AuthorizeTransactionResult execute(AuthorizeTransactionUseCase.AuthorizeTransactionCommand command) {
        Optional<AccountLedger> ledgerOpt = ledgerRepository.findByAccountId(command.accountId());
        
        if (ledgerOpt.isEmpty()) {
            throw new ResourceNotFoundException("Account not found");
        }

        AccountLedger ledger = ledgerOpt.get();
        
        if (ledger.getBalance().compareTo(command.amount()) < 0) {
            throw new BusinessRuleViolationException("Insufficient funds");
        }

        ledger.setBalance(ledger.getBalance().subtract(command.amount()));
        ledger.setUpdatedAt(LocalDateTime.now());
        ledgerRepository.save(ledger);

        UUID transactionId = randomUUID();
        TransactionHistory history = new TransactionHistory(
            transactionId, ledger.getAccountId(), command.amount(), command.type(), "AUTHORIZED", LocalDateTime.now()
        );
        historyRepository.save(history);

        String payload = String.format(
            "{\"eventId\":\"%s\", \"transactionId\":\"%s\", \"accountId\":\"%s\", \"amount\":%s, \"timestamp\":\"%s\"}",
            randomUUID(), transactionId, ledger.getAccountId(), command.amount(), LocalDateTime.now()
        );

        OutboxEvent event = new OutboxEvent(
            randomUUID(), transactionId, "TransactionAuthorizedEvent", payload, false, LocalDateTime.now()
        );
        outboxRepository.save(event);

        return new AuthorizeTransactionUseCase.AuthorizeTransactionResult(transactionId, "AUTHORIZED", "Transaction successful");
    }
}
