package com.redcarepharmacy.githubreposcorer.dto;

import java.io.Serializable;

public record RepositoryScoreDto(
        String name,
        String owner,
        String language,
        int stars,
        int forks,
        double popularityScore
) implements Serializable {
    public static RepositoryScoreDto from(Item repo, double score) {
        return new RepositoryScoreDto(
                repo.name(),
                repo.owner().login(),
                repo.language(),
                repo.stargazersCount(),
                repo.forksCount(),
                score
        );
    }
}