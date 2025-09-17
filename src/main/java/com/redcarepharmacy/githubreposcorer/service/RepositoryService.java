package com.redcarepharmacy.githubreposcorer.service;

import com.redcarepharmacy.githubreposcorer.dto.RepositoryScoreResponseDto;
import java.time.LocalDate;

/**
 * Service interface for fetching repositories from an external source
 * (e.g., GitHub) and calculating their corresponding scores.
 *
 * <p>Implementations of this interface are responsible for integrating
 * with a repository provider, retrieving repositories based on search
 * criteria, and returning a response object containing scored results.</p>
 *
 * @see com.redcarepharmacy.githubreposcorer.service.GithubRepositoryService
 */
public interface RepositoryService {
    /**
     * Fetches repositories by language and creation date, then computes
     * their scores using a scoring strategy.
     *
     * @param language     the programming language to filter repositories by;
     *                     must not be {@code null} or blank
     * @param createdAfter only repositories created after this date will be included
     * @param page         the page number for paginated results; must be >= 1
     * @return a {@link RepositoryScoreResponseDto} containing metadata such as
     *         total count, incomplete results flag, and a list of scored repositories
     */
    RepositoryScoreResponseDto fetchAndScoreRepositories(String language, LocalDate createdAfter, int page);
}
