package com.redcarepharmacy.githubreposcorer.service;

import com.redcarepharmacy.githubreposcorer.dto.GitHubRepositorySearchDto;
import com.redcarepharmacy.githubreposcorer.dto.RepositoryScoreDto;
import com.redcarepharmacy.githubreposcorer.dto.RepositoryScoreResponseDto;
import com.redcarepharmacy.githubreposcorer.exception.GitHubSearchLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Service
public class GithubRepositoryService implements RepositoryService {

    private final RestTemplate restTemplate;
    private final RepositoryScoreCalculator repositoryScoreCalculator;
    private final String githubSearchRepoURL;
    private final Logger logger = LoggerFactory.getLogger(GithubRepositoryService.class);

    public GithubRepositoryService(RestTemplate restTemplate,
                                   @Value("${github.api.search.url}") String githubSearchRepoURL,
                                   RepositoryScoreCalculator repositoryScoreCalculator) {
        this.restTemplate = restTemplate;
        this.githubSearchRepoURL = githubSearchRepoURL;
        this.repositoryScoreCalculator = repositoryScoreCalculator;
    }

    /**
     * Service responsible for fetching GitHub repositories via the GitHub Search API,
     * computing their scores, and returning the results in a structured response format.
     * <p>
     * This class:
     * <ul>
     *   <li>Builds a search request URL using the configured GitHub API endpoint</li>
     *   <li>Calls the GitHub API using {@link RestTemplate}</li>
     *   <li>Handles API errors (e.g., rate limit exceeded)</li>
     *   <li>Computes repository scores using {@link RepositoryScoreCalculator}</li>
     *   <li>Caches results to avoid redundant API calls using Spring’s caching abstraction</li>
     * </ul>
     * <p>
     * Results are returned as {@link RepositoryScoreResponseDto}, which includes:
     * <ul>
     *   <li>Total repository count</li>
     *   <li>Whether results are incomplete</li>
     *   <li>A list of repositories with their computed scores</li>
     * </ul>
     */
    @Cacheable(
            value = "repositories_score",
            key = "#language + ':' + #createdAfter + ':' + #page",
            unless = "#result == null || #result.repositoryScoreList().isEmpty()"
    )
    public RepositoryScoreResponseDto fetchAndScoreRepositories
    (String language, LocalDate createdAfter, int page) {
        String url = String.format(githubSearchRepoURL, language, createdAfter, page);
        logger.info("Fetching repositories from github with the URL:- {}",url);
        GitHubRepositorySearchDto gitResponse;
        try {
            gitResponse = restTemplate
                    .getForObject(url, GitHubRepositorySearchDto.class);
        } catch (HttpClientErrorException.UnprocessableEntity e) {
            String responseBody = e.getResponseBodyAsString();
            logger.error("An exception occurred while " +
                    "fetching repositories from github API",e);
            throw new GitHubSearchLimitExceededException(responseBody);
        }
        if (gitResponse == null || gitResponse.items() == null)
            return new RepositoryScoreResponseDto(0,
                    false, List.of());

        logger.info("Fetching repositories finished, " +
                "total count of repos:- {}",gitResponse.totalCount());
        List<RepositoryScoreDto> repoScoreList = gitResponse.items().stream()
                .map(repo ->
                        RepositoryScoreDto.from(repo,
                                repositoryScoreCalculator.computeScore(
                                        repo.stargazersCount(),
                                        repo.forksCount(),
                                        repo.updatedAt()))
                ).toList();
        return new RepositoryScoreResponseDto(
                gitResponse.totalCount(), gitResponse.incompleteResults(),
                repoScoreList);

    }
}