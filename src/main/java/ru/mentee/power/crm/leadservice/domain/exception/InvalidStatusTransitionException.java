package ru.mentee.power.crm.leadservice.domain.exception;

import ru.mentee.power.crm.leadservice.domain.model.LeadStatus;

public class InvalidStatusTransitionException extends RuntimeException {
  public InvalidStatusTransitionException(LeadStatus current, LeadStatus attempted) {
    super("Invalid status transition from " + current + " to " + attempted);
  }
}
