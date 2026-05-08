package ru.mentee.power.crm.leadservice.adapter.in.rest;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class LeadResponse {
  private UUID id;
  private String title;
  private String description;
  private String status;
  private UUID personId;
  private String source;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
