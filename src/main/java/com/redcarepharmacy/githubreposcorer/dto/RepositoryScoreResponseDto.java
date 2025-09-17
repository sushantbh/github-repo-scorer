package com.redcarepharmacy.githubreposcorer.dto;

import java.io.Serializable;
import java.util.List;

public record RepositoryScoreResponseDto(
        int totalCount,
        boolean incompleteResults,
        List<RepositoryScoreDto> repositoryScoreList
) implements Serializable {
}