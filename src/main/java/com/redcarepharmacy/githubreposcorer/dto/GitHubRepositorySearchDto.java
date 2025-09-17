package com.redcarepharmacy.githubreposcorer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GitHubRepositorySearchDto(
        List<Item> items,
        @JsonProperty("total_count") int totalCount,
        @JsonProperty("incomplete_results") boolean incompleteResults
) {
}
