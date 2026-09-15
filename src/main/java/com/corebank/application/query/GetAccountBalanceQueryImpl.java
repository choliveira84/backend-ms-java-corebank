package com.corebank.application.query;

import com.corebank.domain.account.BalanceRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GetAccountBalanceQueryImpl implements GetAccountBalanceQuery {

    private final BalanceRepository balanceRepository;

    public GetAccountBalanceQueryImpl(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    @Override
    public Optional<Result> execute(UUID accountId) {
        return balanceRepository.getBalance(accountId)
                .map(projection -> new Result(
                        projection.accountId(),
                        projection.availableBalance(),
                        projection.lastUpdatedAt()
                ));
    }
}
