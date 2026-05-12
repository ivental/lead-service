package ru.mentee.power.crm.leadservice.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class LeadStatusHistory {
  private UUID leadId;
  private LeadStatus fromStatus;
  private LeadStatus toStatus;
  private LocalDateTime changedAt;

  public LeadStatusHistory() {}

  public UUID getLeadId() {
    return leadId;
  }

  public void setLeadId(UUID leadId) {
    this.leadId = leadId;
  }

  public LeadStatus getFromStatus() {
    return fromStatus;
  }

  public void setFromStatus(LeadStatus fromStatus) {
    this.fromStatus = fromStatus;
  }

  public LeadStatus getToStatus() {
    return toStatus;
  }

  public void setToStatus(LeadStatus toStatus) {
    this.toStatus = toStatus;
  }

  public LocalDateTime getChangedAt() {
    return changedAt;
  }

  public void setChangedAt(LocalDateTime changedAt) {
    this.changedAt = changedAt;
  }
}
