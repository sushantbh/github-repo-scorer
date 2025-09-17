package com.redcarepharmacy.githubreposcorer.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.redis.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryCacheConfig {

    @Bean
    public CacheManager simpleCacheManager() {
        return new ConcurrentMapCacheManager("repositories_score");
    }
}