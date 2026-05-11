package ru.mentee.power.crm.leadservice.adapter.in.rest.error;

import java.time.LocalDateTime;

public record ErrorResponse(
    String type,
    String title,
    int status,
    String detail,
    String instance,
    String service,
    String errorCode,
    LocalDateTime timestamp,
    String traceId) {
  public static ErrorResponse of(
      String type, String title, int status, String detail, String instance, String errorCode) {
    return new ErrorResponse(
        type, title, status, detail, instance, "lead-service", errorCode, LocalDateTime.now(), "");
  }
}
