package ru.mentee.power.crm.leadservice.domain.exception;

import java.util.UUID;

public class LeadNotFoundException extends RuntimeException {
  public LeadNotFoundException(UUID id) {
    super("Lead not found with id: " + id);
  }
}
