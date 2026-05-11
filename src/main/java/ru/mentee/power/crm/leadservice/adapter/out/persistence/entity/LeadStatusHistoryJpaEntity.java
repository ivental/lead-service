package ru.mentee.power.crm.leadservice.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lead_status_history")
public class LeadStatusHistoryJpaEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Version
  @Column(nullable = false)
  private Long version = 0L;

  @Column(name = "lead_id", nullable = false)
  private UUID leadId;

  @Enumerated(EnumType.STRING)
  @Column(name = "from_status")
  private LeadJpaStatus fromStatus;

  @Enumerated(EnumType.STRING)
  @Column(name = "to_status", nullable = false)
  private LeadJpaStatus toStatus;

  @Column(name = "changed_at", nullable = false)
  private LocalDateTime changedAt;

  public LeadStatusHistoryJpaEntity() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public UUID getLeadId() {
    return leadId;
  }

  public void setLeadId(UUID leadId) {
    this.leadId = leadId;
  }

  public LeadJpaStatus getFromStatus() {
    return fromStatus;
  }

  public void setFromStatus(LeadJpaStatus fromStatus) {
    this.fromStatus = fromStatus;
  }

  public LeadJpaStatus getToStatus() {
    return toStatus;
  }

  public void setToStatus(LeadJpaStatus toStatus) {
    this.toStatus = toStatus;
  }

  public LocalDateTime getChangedAt() {
    return changedAt;
  }

  public void setChangedAt(LocalDateTime changedAt) {
    this.changedAt = changedAt;
  }
}
