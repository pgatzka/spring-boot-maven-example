
package io.github.pgatzka.example.rest;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                    @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                        .filter(fieldError -> fieldError.getDefaultMessage() != null).collect(Collectors.toMap(
                                        FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage));

        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setProperty("errors", errors);

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleExceptionInternal(@NonNull Exception ex, @Nullable Object body,
                    @NonNull HttpHeaders headers, @NonNull HttpStatusCode statusCode, @NonNull WebRequest request) {
        ResponseEntity<Object> response = super.handleExceptionInternal(ex, body, headers, statusCode, request);

        if (response != null && response.getBody() instanceof ProblemDetail problem) {
            problem.setProperty("timestamp", Instant.now());
        }

        logException(ex, statusCode, request);

        return response;
    }

    private void logException(Exception ex, HttpStatusCode status, WebRequest request) {
        String path = request.getDescription(false);

        if (status.is5xxServerError()) {
            log.error("{} on {}: {}", status.value(), path, ex.getMessage(), ex);
        } else if (status.is4xxClientError()) {
            log.debug("{} on {}: {}", status.value(), path, ex.getMessage());
        }
    }

}
