package com.corebank.application.event;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectionConsistencyPolicyTest {

    private static final Instant NOW = Instant.parse("2026-09-15T12:00:00Z");
    private final ProjectionConsistencyPolicy policy = new ProjectionConsistencyPolicy(
            Duration.ofSeconds(5), Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void shouldAcceptProjectionAtFiveSecondBoundary() {
        LocalDateTime observedAt = LocalDateTime.ofInstant(NOW.minusSeconds(5), ZoneOffset.UTC);

        assertTrue(policy.isWithinTolerance(observedAt));
        assertFalse(policy.isStale(observedAt));
    }

    @Test
    void shouldFlagProjectionOlderThanFiveSeconds() {
        LocalDateTime observedAt = LocalDateTime.ofInstant(NOW.minusSeconds(6), ZoneOffset.UTC);

        assertTrue(policy.isStale(observedAt));
        assertFalse(policy.isWithinTolerance(observedAt));
    }

    @Test
    void shouldExposeFreshnessDetailsForOperationalChecks() {
        LocalDateTime observedAt = LocalDateTime.ofInstant(NOW.minusSeconds(6), ZoneOffset.UTC);

        ProjectionFreshness freshness = policy.freshnessOf(observedAt);

        assertTrue(freshness.stale());
        assertFalse(freshness.withinTolerance());
        assertEquals(Duration.ofSeconds(6), freshness.age());
        assertEquals(Duration.ofSeconds(5), freshness.maxDelay());
    }
}
