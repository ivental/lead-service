package ru.mentee.power.crm.leadservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LeadTest {

  @Test
  void shouldCreateLeadWithNoArgsConstructor() {
    Lead lead = new Lead();
    assertThat(lead).isNotNull();
  }

  @Test
  void shouldCreateLeadWithAllArgsConstructor() {
    UUID id = UUID.randomUUID();
    UUID personId = UUID.randomUUID();
    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime updatedAt = LocalDateTime.now();
    Lead lead =
        new Lead(
            id,
            "Test Title",
            "Test Description",
            LeadStatus.NEW,
            personId,
            "website",
            createdAt,
            updatedAt);

    assertThat(lead.getId()).isEqualTo(id);
    assertThat(lead.getTitle()).isEqualTo("Test Title");
    assertThat(lead.getDescription()).isEqualTo("Test Description");
    assertThat(lead.getStatus()).isEqualTo(LeadStatus.NEW);
    assertThat(lead.getPersonId()).isEqualTo(personId);
    assertThat(lead.getSource()).isEqualTo("website");
    assertThat(lead.getCreatedAt()).isEqualTo(createdAt);
    assertThat(lead.getUpdatedAt()).isEqualTo(updatedAt);
  }

  @Test
  void shouldSetAndGetId() {
    Lead lead = new Lead();
    UUID id = UUID.randomUUID();
    lead.setId(id);
    assertThat(lead.getId()).isEqualTo(id);
  }

  @Test
  void shouldSetAndGetTitle() {
    Lead lead = new Lead();
    lead.setTitle("New Lead");
    assertThat(lead.getTitle()).isEqualTo("New Lead");
  }

  @Test
  void shouldSetAndGetDescription() {
    Lead lead = new Lead();
    lead.setDescription("Lead description");
    assertThat(lead.getDescription()).isEqualTo("Lead description");
  }

  @Test
  void shouldSetAndGetStatus() {
    Lead lead = new Lead();
    lead.setStatus(LeadStatus.CONTACTED);
    assertThat(lead.getStatus()).isEqualTo(LeadStatus.CONTACTED);
  }

  @Test
  void shouldSetAndGetPersonId() {
    Lead lead = new Lead();
    UUID personId = UUID.randomUUID();
    lead.setPersonId(personId);
    assertThat(lead.getPersonId()).isEqualTo(personId);
  }

  @Test
  void shouldSetAndGetSource() {
    Lead lead = new Lead();
    lead.setSource("referral");
    assertThat(lead.getSource()).isEqualTo("referral");
  }

  @Test
  void shouldSetAndGetCreatedAt() {
    Lead lead = new Lead();
    LocalDateTime createdAt = LocalDateTime.now();
    lead.setCreatedAt(createdAt);
    assertThat(lead.getCreatedAt()).isEqualTo(createdAt);
  }

  @Test
  void shouldSetAndGetUpdatedAt() {
    Lead lead = new Lead();
    LocalDateTime updatedAt = LocalDateTime.now();
    lead.setUpdatedAt(updatedAt);
    assertThat(lead.getUpdatedAt()).isEqualTo(updatedAt);
  }

  @Test
  void shouldTestEqualsAndHashCode() {
    UUID id = UUID.randomUUID();
    Lead lead1 = new Lead(id, "Title", "Desc", LeadStatus.NEW, null, "web", null, null);
    Lead lead2 = new Lead(id, "Title", "Desc", LeadStatus.NEW, null, "web", null, null);

    assertThat(lead1).isEqualTo(lead2);
    assertThat(lead1.hashCode()).isEqualTo(lead2.hashCode());
  }

  @Test
  void shouldGenerateToString() {
    Lead lead = new Lead();
    lead.setTitle("Test");
    assertThat(lead.toString()).contains("Test");
  }
}
