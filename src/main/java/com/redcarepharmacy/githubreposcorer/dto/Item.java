package com.redcarepharmacy.githubreposcorer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record Item(
        Long id,
        String name,
        String language,
        @JsonProperty("stargazers_count") int stargazersCount,
        @JsonProperty("forks_count") int forksCount,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("created_at") Instant createdAt,
        Owner owner
) {
}