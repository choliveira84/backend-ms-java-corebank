package com.corebank.application.query;

import com.corebank.domain.account.BalanceProjection;
import com.corebank.infrastructure.persistence.BalanceRedisRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class GetBalanceQuery {

    private final BalanceRedisRepository repository;

    public GetBalanceQuery(BalanceRedisRepository repository) {
        this.repository = repository;
    }

    public Optional<BalanceProjection> execute(UUID accountId) {
        return repository.getBalance(accountId);
    }
}
