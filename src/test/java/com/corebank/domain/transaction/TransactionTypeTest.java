package com.corebank.domain.transaction;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class TransactionTypeTest {

    @Test
    void shouldExposeOnlySupportedTransactionTypes() {
        assertArrayEquals(new TransactionType[] {
                TransactionType.DEBIT,
                TransactionType.TRANSFER,
                TransactionType.PIX
        }, TransactionType.values());
    }
}