package com.redcarepharmacy.githubreposcorer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.Instant;

public record RepositoryScoreDto(
        String name,
        String owner,
        String language,
        int stars,
        int forks,
        Instant createdAt,
        Instant updatedAt,
        double popularityScore
) implements Serializable {
    public static RepositoryScoreDto from(Item repo, double score) {
        return new RepositoryScoreDto(
                repo.name(),
                repo.owner().login(),
                repo.language(),
                repo.stargazersCount(),
                repo.forksCount(),
                repo.createdAt(),
                repo.updatedAt(),
                score
        );
    }
}