package ru.mentee.power.crm.leadservice.domain.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import ru.mentee.power.crm.leadservice.domain.exception.InvalidStatusTransitionException;

public class Lead {
  private static final Map<LeadStatus, Set<LeadStatus>> VALID_TRANSITIONS =
      Map.of(
          LeadStatus.NEW, Set.of(LeadStatus.CONTACTED, LeadStatus.DISQUALIFIED),
          LeadStatus.CONTACTED, Set.of(LeadStatus.QUALIFIED, LeadStatus.DISQUALIFIED));

  public void changeStatus(LeadStatus newStatus) {
    if (this.status == newStatus) {
      return;
    }
    if (!VALID_TRANSITIONS.getOrDefault(this.status, Set.of()).contains(newStatus)) {
      throw new InvalidStatusTransitionException(this.status, newStatus);
    }
    this.status = newStatus;
    this.updatedAt = LocalDateTime.now();
  }

  private UUID id;
  private String title;
  private String description;
  private LeadStatus status;
  private UUID personId;
  private String source;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Lead() {}

  Lead(
      UUID id,
      String title,
      String description,
      LeadStatus status,
      UUID personId,
      String source,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.status = status;
    this.personId = personId;
    this.source = source;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static Lead createNew(String title, String description, UUID personId, String source) {
    Lead lead = new Lead();
    lead.id = UUID.randomUUID();
    lead.title = title;
    lead.description = description;
    lead.status = LeadStatus.NEW;
    lead.personId = personId;
    lead.source = source;
    lead.createdAt = LocalDateTime.now();
    lead.updatedAt = LocalDateTime.now();
    return lead;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public LeadStatus getStatus() {
    return status;
  }

  public void setStatus(LeadStatus status) {
    this.status = status;
  }

  public UUID getPersonId() {
    return personId;
  }

  public void setPersonId(UUID personId) {
    this.personId = personId;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
