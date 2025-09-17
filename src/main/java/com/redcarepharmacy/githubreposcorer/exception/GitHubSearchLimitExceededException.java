package com.redcarepharmacy.githubreposcorer.exception;

public class GitHubSearchLimitExceededException extends RuntimeException {
    public GitHubSearchLimitExceededException(String message) {
        super(message);
    }
}