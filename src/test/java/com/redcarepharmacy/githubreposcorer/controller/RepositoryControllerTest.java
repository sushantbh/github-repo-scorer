package com.redcarepharmacy.githubreposcorer.controller;

import com.redcarepharmacy.githubreposcorer.dto.RepositoryScoreDto;
import com.redcarepharmacy.githubreposcorer.dto.RepositoryScoreResponseDto;
import com.redcarepharmacy.githubreposcorer.service.RepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RepositoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RepositoryService gitHubRepositoryService;

    private RepositoryScoreResponseDto repoDto;

    @BeforeEach
    void setUp() {
        repoDto = new RepositoryScoreResponseDto(1,
                false,
                List.of(
                        new RepositoryScoreDto(
                                "repo1", "user1", "java",
                                10, 5, 2.5)));
    }

    @Test
    void testGetRepositoriesScore_success() throws Exception {
        Mockito.when(gitHubRepositoryService.fetchAndScoreRepositories(
                        anyString(), any(LocalDate.class), anyInt()))
                .thenReturn(repoDto);

        mockMvc.perform(get("/api/v1/repositories/score")
                        .param("language", "java")
                        .param("createdAfter", "2023-01-01")
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repositoryScoreList.[0].name").value("repo1"))
                .andExpect(jsonPath("$.repositoryScoreList.[0].language").value("java"))
                .andExpect(jsonPath("$.repositoryScoreList.[0].popularityScore").value(2.5));
    }

    @Test
    void testGetRepositoriesScore_missingLanguage_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/repositories/score")
                        .param("createdAfter", "2023-01-01")
                        .param("page", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testGetRepositoriesScore_invalidPage_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/repositories/score")
                        .param("language", "java")
                        .param("createdAfter", "2023-01-01")
                        .param("page", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testGetRepositoriesScore_invalidDate_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/repositories/score")
                        .param("language", "java")
                        .param("createdAfter", "not-a-date")
                        .param("page", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
