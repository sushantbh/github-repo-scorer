package com.redcarepharmacy.githubreposcorer.service;
import com.redcarepharmacy.githubreposcorer.config.RepositoryScoringProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class WeightedRepositoryScoreCalculatorTest {

    private WeightedRepositoryScoreCalculator calculator;

    @BeforeEach
    void setup() {
        RepositoryScoringProperties properties = Mockito.mock(RepositoryScoringProperties.class);

        when(properties.weightStars()).thenReturn(0.3);
        when(properties.weightForks()).thenReturn(0.5);
        when(properties.weightRecent()).thenReturn(0.2);
        when(properties.recencyBase()).thenReturn(100.0);

        calculator = new WeightedRepositoryScoreCalculator(properties);
    }

    @Test
    void testComputeScore_sameRepos_sameScore() {
        Instant now = Instant.now();
        double score1 = calculator.computeScore(10, 5, now);
        double score2 = calculator.computeScore(10, 5, now);

        assertEquals(score1, score2, "Two repos with same properties should have same score");
    }

    @Test
    void testComputeScore_recencyEffect() {
        Instant today = Instant.now();
        Instant tenDaysAgo = today.minusSeconds(10 * 24 * 3600);

        double recentScore = calculator.computeScore(10, 5, today);
        double olderScore = calculator.computeScore(10, 5, tenDaysAgo);

        assertTrue(olderScore < recentScore, "Older repo should have lower score due to recency");
    }

    @Test
    void testComputeScore_recentRepoHasHigherScore() {
        int stars = 10;
        int forks = 5;
        Instant updatedAt = Instant.now().minus(1, ChronoUnit.DAYS);

        double score = calculator.computeScore(stars, forks, updatedAt);

        double recencyScore = Math.max(0, 100.0 - 1);
        double expectedScore = (stars * 0.3) + (forks * 0.5) + (recencyScore * 0.2);
        expectedScore = Math.round(expectedScore * 100.0) / 100.0;

        assertEquals(expectedScore, score);
    }

    @Test
    void testComputeScore_zeroValues_nullUpdatedAt() {
        int stars = 0;
        int forks = 0;
        double score = calculator.computeScore(stars, forks, null);
        assertEquals(0, score);
    }
}
