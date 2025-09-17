package com.redcarepharmacy.githubreposcorer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "repository.scoring")
public record RepositoryScoringProperties(

        double weightStars,
        double weightForks,
        double weightRecent,
        double recencyBase
) {
}
