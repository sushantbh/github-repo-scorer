
# GitHub Repo Scorer

A Spring Boot application that fetches GitHub repositories for a given language and earliest creation date, computes a weighted score based on stars, forks, and recency, and caches the results using Spring Cache (Redis support available).

## Features

- Fetch GitHub repositories via GitHub API
- Compute repository scores based on:
    - Stars
    - Forks
    - Recency
- Caching with Spring Cache (in-memory or Redis)
- Handles API rate limits and GitHub search limits gracefully
- Configurable scoring weights and recency base via config file.

## Requirements

- Java 21
- Maven
- Redis (optional, if using Redis cache)
- Internet connection for GitHub API

## Installation

1. Clone the repository:

```bash
git clone <repository-url>
cd github-repo-scorer
```

2. Build the project:

```bash
mvn clean install
```

3. Run the application:

```bash
mvn spring-boot:run
```

## Configuration

All configuration is in `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: "GitHub Repo Scorer"
github:
  api:
    search:
      url: "https://api.github.com/search/repositories?q=language:%s+created:>%s&page=%s"
repository:
  scoring:
    weight-stars: 0.3
    weight-forks: 0.5
    weight-recent: 0.2
    recency-base: 100
app:
  redis:
    enabled: true
    host: localhost
    port: 6379
    ttl-hours: 6
```

- `weight-stars`, `weight-forks`, `weight-recent`: scoring weights
- `recency-base`: base value for recency calculation
- Redis caching is optional; disable with `app.redis.enabled=false`
- If Redis is needed, set `app.redis.enabled=true` and update the redis-server configuration in `application.yml`
## API

### GET `/api/v1/repositories/score`

Fetch repositories and their scores.

**Query Parameters:**

- `language` (required): Programming language
- `createdAfter` (required, ISO date): Only fetch repositories created after this date
- `page` (optional, default 1): GitHub API page number

**Example Request:**

```
GET /api/v1/repositories/score?language=java&createdAfter=2023-01-01&page=1
```

**Response:**

```json
{
  "totalCount": 123,
  "incompleteResults": false,
  "items": [
    {
      "name": "example-repo",
      "url": "https://github.com/user/example-repo",
      "score": 25.5
    }
  ]
}
```

## Testing

- Unit tests are written using JUnit 5 and Mockito.
- Run tests:

```bash
mvn test
```

## Caching

- Caching is enabled via Spring Cache.
- Supports in-memory or Redis caches.
- Cache key format: `language:createdAfter:page`
- Cache TTL configurable in `application.yml` (with Redis)

## Exception Handling

- Handles GitHub API rate limits (`429`) and search limits (`422`)
- Provides readable error messages via a global exception handler

## Notes

- Null or missing attributes in GitHub API response are handled gracefully.
- Scores are rounded to 2 decimal places.
- Redis cache is optional but recommended for repeated queries (ttl can be configured)

## Author

Sushant
