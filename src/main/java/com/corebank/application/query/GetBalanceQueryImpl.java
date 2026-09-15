package com.corebank.application.query;

import com.corebank.domain.account.BalanceProjection;
import com.corebank.domain.account.BalanceRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class GetBalanceQueryImpl implements GetBalanceQuery {

    private final BalanceRepository repository;

    public GetBalanceQueryImpl(BalanceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<BalanceProjection> execute(UUID accountId) {
        return repository.getBalance(accountId);
    }
}
