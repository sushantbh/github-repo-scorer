package com.redcarepharmacy.githubreposcorer.service;

import java.time.Instant;

/**
 * Interface for calculating a repository's score based on
 * various attributes such as stars, forks, and last updated date.
 *
 * <p>Implementations of this interface can apply different scoring
 * algorithms (e.g., weighted scoring, custom formulas) to determine
 * the overall repository quality or popularity.</p>
 *
 * @see com.redcarepharmacy.githubreposcorer.service.WeightedRepositoryScoreCalculator
 */
public interface RepositoryScoreCalculator {
    /**
     * Computes a score for a repository based on its metadata.
     *
     * @param stars     the number of stargazers of the repository
     * @param forks     the number of forks of the repository
     * @param updatedAt the timestamp of the last repository update;
     *
     * @return the computed score as a double value
     */
    double computeScore(int stars, int forks, Instant updatedAt);
}
