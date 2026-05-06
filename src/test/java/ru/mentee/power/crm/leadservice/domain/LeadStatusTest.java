package ru.mentee.power.crm.leadservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LeadStatusTest {

  @Test
  void shouldHaveFourStatuses() {
    LeadStatus[] statuses = LeadStatus.values();
    assertThat(statuses).hasSize(4);
  }

  @Test
  void shouldContainNewStatus() {
    assertThat(LeadStatus.valueOf("NEW")).isEqualTo(LeadStatus.NEW);
  }

  @Test
  void shouldContainContactedStatus() {
    assertThat(LeadStatus.valueOf("CONTACTED")).isEqualTo(LeadStatus.CONTACTED);
  }

  @Test
  void shouldContainQualifiedStatus() {
    assertThat(LeadStatus.valueOf("QUALIFIED")).isEqualTo(LeadStatus.QUALIFIED);
  }

  @Test
  void shouldContainDisqualifiedStatus() {
    assertThat(LeadStatus.valueOf("DISQUALIFIED")).isEqualTo(LeadStatus.DISQUALIFIED);
  }

  @Test
  void shouldGetStatusName() {
    assertThat(LeadStatus.NEW.name()).isEqualTo("NEW");
    assertThat(LeadStatus.CONTACTED.name()).isEqualTo("CONTACTED");
    assertThat(LeadStatus.QUALIFIED.name()).isEqualTo("QUALIFIED");
    assertThat(LeadStatus.DISQUALIFIED.name()).isEqualTo("DISQUALIFIED");
  }
}
