package ru.mentee.power.crm.leadservice.adapter.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

public class LeadCreateRequest {
  @NotBlank(message = "Title is required")
  private String title;

  private String description;

  @NotNull(message = "PersonId is required")
  private UUID personId;

  @NotNull(message = "Source is required")
  @Pattern(
      regexp = "^(WEBSITE|EMAIL|PHONE|PARTNER)$",
      message = "Source must be one of: WEBSITE, EMAIL, PHONE, PARTNER")
  private String source;

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public UUID getPersonId() {
    return personId;
  }

  public String getSource() {
    return source;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public void setPersonId(UUID personId) {
    this.personId = personId;
  }
}
