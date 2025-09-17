package com.redcarepharmacy.githubreposcorer.service;


import com.redcarepharmacy.githubreposcorer.config.RepositoryScoringProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;

@Component
public class WeightedRepositoryScoreCalculator implements RepositoryScoreCalculator {

    private final RepositoryScoringProperties properties;

    public WeightedRepositoryScoreCalculator(RepositoryScoringProperties properties) {
        this.properties = properties;
    }

    /**
     * Computes the weighted score of a repository based on stars, forks,
     * and the recency of its last update.
     *
     * <p>The score is calculated using the following formula:</p>
     *
     * <pre>
     * score =
     *   (stars × weightStars)
     * + (forks × weightForks)
     * + (recencyScore × weightRecent)
     * </pre>
     * <p>
     * where:
     * <ul>
     *   <li>{@code stars} — number of stargazers of the repository</li>
     *   <li>{@code forks} — number of forks of the repository</li>
     *   <li>{@code recencyScore} = max(0, recencyBase - daysSinceLastUpdate)</li>
     *   <li>{@code daysSinceLastUpdate} = days between {@code updatedAt} and now</li>
     * </ul>
     *
     * <p>If {@code updatedAt} is {@code null}, it is treated as
     * {@link Instant#EPOCH}, meaning the repository will receive the lowest
     * possible recency score.</p>
     *
     * <p>The final score is rounded to two decimal places using
     * {@link RoundingMode#HALF_UP}.</p>
     *
     * @param stars     number of stars
     * @param forks     number of forks
     * @param updatedAt last update timestamp (maybe {@code null})
     * @return computed repository score, rounded to two decimal places
     */
    @Override
    public double computeScore(int stars, int forks, Instant updatedAt) {
        Instant safeUpdatedAt = (updatedAt != null) ? updatedAt : Instant.EPOCH;
        long daysSinceUpdate = Duration.between(safeUpdatedAt, Instant.now()).toDays();
        double recencyScore = Math.max(0, properties.recencyBase() - daysSinceUpdate);
        return BigDecimal.valueOf((stars * properties.weightStars())
                        + (forks * properties.weightForks())
                        + (recencyScore * properties.weightRecent()))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
