package com.redcarepharmacy.githubreposcorer.service;

import com.redcarepharmacy.githubreposcorer.dto.GitHubRepositorySearchDto;
import com.redcarepharmacy.githubreposcorer.dto.Item;
import com.redcarepharmacy.githubreposcorer.dto.Owner;
import com.redcarepharmacy.githubreposcorer.dto.RepositoryScoreResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GithubRepositoryServiceTest {

    @Mock
    private RestTemplate restTemplate;
    private AutoCloseable mocks;
    @Mock
    private RepositoryScoreCalculator repositoryScoreCalculator;
    private GithubRepositoryService githubRepositoryService;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        githubRepositoryService =
                new GithubRepositoryService(restTemplate,
                        "https://api.github.com/search/repositories?q=language:%s+created:>%s&page=%s",
                        repositoryScoreCalculator);

    }

    @Test
    void testFetchAndScoreRepositories_success() {
        Item repo1 = new Item(
                1L, "repo1", "Java", 5
                , 10, Instant.now(), Instant.now(), new Owner("user1")
        );
        GitHubRepositorySearchDto gitResponse = new GitHubRepositorySearchDto(
                List.of(repo1), 1, false);

        when(restTemplate.getForObject(anyString(), eq(GitHubRepositorySearchDto.class)))
                .thenReturn(gitResponse);
        when(repositoryScoreCalculator.computeScore(5, 10,
                repo1.updatedAt())).thenReturn(42.0);

        RepositoryScoreResponseDto result = githubRepositoryService
                .fetchAndScoreRepositories("java", LocalDate.now(), 1);

        assertEquals(1, result.totalCount());
        assertEquals(42.0, result.repositoryScoreList().getFirst().popularityScore());
    }

    @Test
    void testFetchAndScoreRepositories_nullResponse() {
        when(restTemplate.getForObject(anyString(), eq(GitHubRepositorySearchDto.class)))
                .thenReturn(null);

        RepositoryScoreResponseDto result = githubRepositoryService
                .fetchAndScoreRepositories("java", LocalDate.now(), 1);

        assertEquals(0, result.totalCount());
    }

    @Test
    void testFetchAndScoreRepositories_nullItems() {
        GitHubRepositorySearchDto gitResponse = new GitHubRepositorySearchDto(null,
                0, false);

        when(restTemplate.getForObject(anyString(), eq(GitHubRepositorySearchDto.class)))
                .thenReturn(gitResponse);

        RepositoryScoreResponseDto result = githubRepositoryService
                .fetchAndScoreRepositories("java", LocalDate.now(), 1);

        assertEquals(0, result.totalCount());
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }
}
