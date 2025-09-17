package com.redcarepharmacy.githubreposcorer.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingParam(MissingServletRequestParameterException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Missing request parameter");
        body.put("message", ex.getParameterName() + " parameter is required");
        logger.error("An exception occurred in the APP", ex);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Invalid request parameter type");
        if (ex.getRequiredType() == LocalDate.class) {
            body.put("message", ex.getName() + " must be in format yyyy-MM-dd (e.g. 2025-01-01)");
        } else {
            body.put("message", ex.getName() + " should be of type "
                    + Objects.requireNonNull(ex.getRequiredType()).getSimpleName());
        }
        logger.error("An exception occurred in the APP", ex);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Invalid request parameter");

        StringBuilder messages = new StringBuilder();
        ex.getConstraintViolations().forEach(v ->
                messages.append(v.getMessage())
        );
        body.put("message", messages.toString());
        logger.error("An exception occurred in the APP", ex);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpClientErrorException.Forbidden.class)
    public ResponseEntity<Map<String, Object>> handleRateLimit(
            HttpClientErrorException.Forbidden ex) {
        String responseBody = ex.getResponseBodyAsString();

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
        if (responseBody.contains("API rate limit exceeded")) {
            body.put("error", "Rate Limit Exceeded");
            body.put("message", "API rate limit exceeded. Please try again later.");
        } else {
            body.put("error", "Http Client Error");
            body.put("message", responseBody);
        }
        logger.error("An exception occurred in the APP", ex);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(body);
    }

    @ExceptionHandler(GitHubSearchLimitExceededException.class)
    public ResponseEntity<Object> handleGitHubLimit(GitHubSearchLimitExceededException ex) {
        logger.error("An exception occurred in the APP", ex);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", HttpStatus.UNPROCESSABLE_ENTITY.value(),
                        "error", "GitHub API limit exceeded",
                        "message", ex.getMessage()
                                .replaceAll("^.*\"message\"\\s*:\\s*\"([^\"]+)\".*$",
                                        "$1")
                ));
    }

    /**
     * Catch-all handler for any unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        logger.error("An exception occurred in the APP", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "error", "Internal Server Error",
                        "message", ex.getMessage() != null ?
                                ex.getMessage() :
                                "Something went wrong. Please try again later."
                ));
    }
}
