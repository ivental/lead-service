package ru.mentee.power.crm.leadservice.adapter.in.rest.error;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.mentee.power.crm.leadservice.domain.exception.InvalidStatusTransitionException;
import ru.mentee.power.crm.leadservice.domain.exception.LeadNotFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(
      IllegalArgumentException ex, WebRequest request) {

    ErrorResponse error =
        ErrorResponse.of(
            "/problems/bad-request",
            "Bad Request",
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            getPath(request),
            "ILLEGAL_ARGUMENT");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex, WebRequest request) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    ErrorResponse error =
        ErrorResponse.of(
            "/problems/validation-error",
            "Validation Failed",
            HttpStatus.BAD_REQUEST.value(),
            errors.toString(),
            getPath(request),
            "VALIDATION_ERROR");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(InvalidStatusTransitionException.class)
  public ResponseEntity<ErrorResponse> handleInvalidTransition(
      InvalidStatusTransitionException ex, WebRequest request) {

    ErrorResponse error =
        ErrorResponse.of(
            "/problems/invalid-transition",
            "Invalid Status Transition",
            HttpStatus.CONFLICT.value(),
            ex.getMessage(),
            getPath(request),
            "INVALID_STATUS_TRANSITION");
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(LeadNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleLeadNotFound(
      LeadNotFoundException ex, WebRequest request) {

    ErrorResponse error =
        ErrorResponse.of(
            "/problems/not-found",
            "Not Found",
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            getPath(request),
            "LEAD_NOT_FOUND");
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {

    log.error("Unexpected error", ex);

    ErrorResponse error =
        ErrorResponse.of(
            "/problems/internal-server-error",
            "Internal Server Error",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getMessage(),
            getPath(request),
            "INTERNAL_ERROR");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

  private String getPath(WebRequest request) {
    return request.getDescription(false).replace("uri=", "");
  }
}
