package ru.mentee.power.crm.leadservice.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lead_status_history")
public class LeadStatusHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "lead_id")
  private Lead lead;

  @Enumerated(EnumType.STRING)
  private LeadStatus fromStatus;

  @Enumerated(EnumType.STRING)
  private LeadStatus toStatus;

  @Column(name = "changed_at")
  private LocalDateTime changeAt;
}
