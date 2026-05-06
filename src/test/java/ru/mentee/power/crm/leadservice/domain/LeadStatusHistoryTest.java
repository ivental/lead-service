package ru.mentee.power.crm.leadservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LeadStatusHistoryTest {

  @Test
  void shouldCreateLeadStatusHistoryWithNoArgsConstructor() {
    LeadStatusHistory history = new LeadStatusHistory();
    assertThat(history).isNotNull();
  }

  @Test
  void shouldCreateLeadStatusHistoryWithAllArgsConstructor() {
    UUID id = UUID.randomUUID();
    Lead lead = new Lead();
    lead.setId(UUID.randomUUID());
    LocalDateTime changeAt = LocalDateTime.now();
    LeadStatusHistory history =
        new LeadStatusHistory(id, lead, LeadStatus.NEW, LeadStatus.CONTACTED, changeAt);

    assertThat(history.getId()).isEqualTo(id);
    assertThat(history.getLead()).isEqualTo(lead);
    assertThat(history.getFromStatus()).isEqualTo(LeadStatus.NEW);
    assertThat(history.getToStatus()).isEqualTo(LeadStatus.CONTACTED);
    assertThat(history.getChangeAt()).isEqualTo(changeAt);
  }

  @Test
  void shouldSetAndGetId() {
    LeadStatusHistory history = new LeadStatusHistory();
    UUID id = UUID.randomUUID();
    history.setId(id);
    assertThat(history.getId()).isEqualTo(id);
  }

  @Test
  void shouldSetAndGetLead() {
    LeadStatusHistory history = new LeadStatusHistory();
    Lead lead = new Lead();
    lead.setId(UUID.randomUUID());
    history.setLead(lead);
    assertThat(history.getLead()).isEqualTo(lead);
  }

  @Test
  void shouldSetAndGetFromStatus() {
    LeadStatusHistory history = new LeadStatusHistory();
    history.setFromStatus(LeadStatus.NEW);
    assertThat(history.getFromStatus()).isEqualTo(LeadStatus.NEW);
  }

  @Test
  void shouldSetAndGetToStatus() {
    LeadStatusHistory history = new LeadStatusHistory();
    history.setToStatus(LeadStatus.QUALIFIED);
    assertThat(history.getToStatus()).isEqualTo(LeadStatus.QUALIFIED);
  }

  @Test
  void shouldSetAndGetChangeAt() {
    LeadStatusHistory history = new LeadStatusHistory();
    LocalDateTime changeAt = LocalDateTime.now();
    history.setChangeAt(changeAt);
    assertThat(history.getChangeAt()).isEqualTo(changeAt);
  }

  @Test
  void shouldTestEqualsAndHashCode() {
    UUID id = UUID.randomUUID();
    Lead lead = new Lead();
    LocalDateTime changeAt = LocalDateTime.now();
    LeadStatusHistory history1 =
        new LeadStatusHistory(id, lead, LeadStatus.NEW, LeadStatus.CONTACTED, changeAt);
    LeadStatusHistory history2 =
        new LeadStatusHistory(id, lead, LeadStatus.NEW, LeadStatus.CONTACTED, changeAt);

    assertThat(history1).isEqualTo(history2);
    assertThat(history1.hashCode()).isEqualTo(history2.hashCode());
  }

  @Test
  void shouldGenerateToString() {
    LeadStatusHistory history = new LeadStatusHistory();
    history.setToStatus(LeadStatus.QUALIFIED);
    assertThat(history.toString()).contains("QUALIFIED");
  }
}
