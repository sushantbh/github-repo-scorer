package com.redcarepharmacy.githubreposcorer;

import com.redcarepharmacy.githubreposcorer.config.RepositoryScoringProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableConfigurationProperties(RepositoryScoringProperties.class)
@EnableCaching
public class RepositoryScoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(RepositoryScoringApplication.class, args);
    }

}
