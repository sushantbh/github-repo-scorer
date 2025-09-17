package com.redcarepharmacy.githubreposcorer.controller;

import com.redcarepharmacy.githubreposcorer.dto.RepositoryScoreResponseDto;
import com.redcarepharmacy.githubreposcorer.service.RepositoryService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/repositories")
@Validated
public class RepositoryController {

    @Autowired
    private RepositoryService gitHubRepositoryService;

    private static final Logger logger = LoggerFactory.getLogger(RepositoryController.class);
    /**
     * Fetch GitHub repositories by programming language and earliest creation date,
     * then return them with calculated scores.
     * <p>
     * Example request:
     * GET /api/v1/repositories/score?language=java&createdAfter=2023-01-01&page=1
     *
     * @param language     Programming language to filter repositories
     * @param createdAfter Earliest creation date of repositories
     * @param page         Page number for pagination (default = 1)
     * @return List of repositories with their calculated scores
     */
    @GetMapping("/score")
    public RepositoryScoreResponseDto getRepositoriesScore(
            @RequestParam
            @NotBlank(message = "Language must not be empty") String language,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdAfter,
            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "Page number must be at least 1") int page) {

        logger.info("Fetching repository scores for language={} createdAfter={} page={}"
                , language, createdAfter, page);
        return gitHubRepositoryService.fetchAndScoreRepositories(language, createdAfter, page);
    }
}