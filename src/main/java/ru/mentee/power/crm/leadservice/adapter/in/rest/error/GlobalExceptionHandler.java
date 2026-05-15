package ru.mentee.power.crm.leadservice.adapter.in.rest.error;

import java.time.LocalDateTime;
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
import ru.mentee.power.crm.leadservice.adapter.in.rest.dto.ErrorResponse;
import ru.mentee.power.crm.leadservice.domain.exception.InvalidStatusTransitionException;
import ru.mentee.power.crm.leadservice.domain.exception.LeadNotFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(
      IllegalArgumentException ex, WebRequest request) {

    ErrorResponse error = new ErrorResponse();
    error.setType("/problems/bad-request");
    error.setTitle("Bad Request");
    error.setStatus(HttpStatus.BAD_REQUEST.value());
    error.setDetail(ex.getMessage());
    error.setInstance(getPath(request));
    error.setService("lead-service");
    error.setErrorCode("ILLEGAL_ARGUMENT");
    error.setTimestamp(LocalDateTime.now());
    error.setTraceId("");

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

    ErrorResponse error = new ErrorResponse();
    error.setType("/problems/validation-error");
    error.setTitle("Validation Failed");
    error.setStatus(HttpStatus.BAD_REQUEST.value());
    error.setDetail(errors.toString());
    error.setInstance(getPath(request));
    error.setService("lead-service");
    error.setErrorCode("VALIDATION_ERROR");
    error.setTimestamp(LocalDateTime.now());
    error.setTraceId("");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(InvalidStatusTransitionException.class)
  public ResponseEntity<ErrorResponse> handleInvalidTransition(
      InvalidStatusTransitionException ex, WebRequest request) {

    ErrorResponse error = new ErrorResponse();
    error.setType("/problems/invalid-transition");
    error.setTitle("Invalid Status Transition");
    error.setStatus(HttpStatus.CONFLICT.value());
    error.setDetail(ex.getMessage());
    error.setInstance(getPath(request));
    error.setService("lead-service");
    error.setErrorCode("INVALID_STATUS_TRANSITION");
    error.setTimestamp(LocalDateTime.now());
    error.setTraceId("");

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(LeadNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleLeadNotFound(
      LeadNotFoundException ex, WebRequest request) {

    ErrorResponse error = new ErrorResponse();
    error.setType("/problems/not-found");
    error.setTitle("Not Found");
    error.setStatus(HttpStatus.NOT_FOUND.value());
    error.setDetail(ex.getMessage());
    error.setInstance(getPath(request));
    error.setService("lead-service");
    error.setErrorCode("LEAD_NOT_FOUND");
    error.setTimestamp(LocalDateTime.now());
    error.setTraceId("");

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {

    log.error("Unexpected error", ex);

    ErrorResponse error = new ErrorResponse();
    error.setType("/problems/internal-server-error");
    error.setTitle("Internal Server Error");
    error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    error.setDetail(ex.getMessage());
    error.setInstance(getPath(request));
    error.setService("lead-service");
    error.setErrorCode("INTERNAL_ERROR");
    error.setTimestamp(LocalDateTime.now());
    error.setTraceId("");

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

  @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
      org.springframework.http.converter.HttpMessageNotReadableException ex, WebRequest request) {

    ErrorResponse error = new ErrorResponse();
    error.setType("/problems/bad-request");
    error.setTitle("Bad Request");
    error.setStatus(HttpStatus.BAD_REQUEST.value());
    error.setDetail("Invalid status value. Allowed: NEW, CONTACTED, QUALIFIED, DISQUALIFIED");
    error.setInstance(getPath(request));
    error.setService("lead-service");
    error.setErrorCode("INVALID_STATUS_VALUE");
    error.setTimestamp(LocalDateTime.now());
    error.setTraceId("");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  private String getPath(WebRequest request) {
    return request.getDescription(false).replace("uri=", "");
  }
}
