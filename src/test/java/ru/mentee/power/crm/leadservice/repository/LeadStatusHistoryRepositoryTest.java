package ru.mentee.power.crm.leadservice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertySource;
import ru.mentee.power.crm.leadservice.domain.Lead;
import ru.mentee.power.crm.leadservice.domain.LeadStatus;
import ru.mentee.power.crm.leadservice.domain.LeadStatusHistory;

@DataJpaTest
class LeadStatusHistoryRepositoryTest {

  @DynamicPropertySource
  static void configureProperties(
      org.springframework.test.context.DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5433/lead_db");
    registry.add("spring.datasource.username", () -> "postgres");
    registry.add("spring.datasource.password", () -> "postgres");
    registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    registry.add("spring.jpa.show-sql", () -> "true");
    registry.add("spring.liquibase.enabled", () -> "false");
  }

  @Autowired private LeadStatusHistoryRepository leadStatusHistoryRepository;

  @Autowired private LeadRepository leadRepository;

  @Test
  void shouldSaveAndFindHistoryById() {
    Lead lead = new Lead();
    lead.setTitle("Test Lead");
    lead.setStatus(LeadStatus.NEW);
    Lead savedLead = leadRepository.save(lead);

    LeadStatusHistory history = new LeadStatusHistory();
    history.setLead(savedLead);
    history.setFromStatus(LeadStatus.NEW);
    history.setToStatus(LeadStatus.CONTACTED);
    history.setChangeAt(LocalDateTime.now());

    LeadStatusHistory saved = leadStatusHistoryRepository.save(history);
    Optional<LeadStatusHistory> found = leadStatusHistoryRepository.findById(saved.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getLead().getId()).isEqualTo(savedLead.getId());
    assertThat(found.get().getFromStatus()).isEqualTo(LeadStatus.NEW);
    assertThat(found.get().getToStatus()).isEqualTo(LeadStatus.CONTACTED);
  }

  @Test
  void shouldSaveMultipleHistoryEntriesForLead() {
    Lead lead = new Lead();
    lead.setTitle("Lead with History");
    lead.setStatus(LeadStatus.NEW);
    Lead savedLead = leadRepository.save(lead);

    LeadStatusHistory history1 = new LeadStatusHistory();
    history1.setLead(savedLead);
    history1.setFromStatus(LeadStatus.NEW);
    history1.setToStatus(LeadStatus.CONTACTED);
    history1.setChangeAt(LocalDateTime.now());
    leadStatusHistoryRepository.save(history1);

    LeadStatusHistory history2 = new LeadStatusHistory();
    history2.setLead(savedLead);
    history2.setFromStatus(LeadStatus.CONTACTED);
    history2.setToStatus(LeadStatus.QUALIFIED);
    history2.setChangeAt(LocalDateTime.now());
    leadStatusHistoryRepository.save(history2);

    List<LeadStatusHistory> allHistory = leadStatusHistoryRepository.findAll();

    assertThat(allHistory).hasSize(2);
    assertThat(allHistory).allMatch(h -> h.getLead().getId().equals(savedLead.getId()));
  }

  @Test
  void shouldUpdateHistoryEntry() {
    Lead lead = new Lead();
    lead.setTitle("Lead for Update");
    lead.setStatus(LeadStatus.NEW);
    Lead savedLead = leadRepository.save(lead);

    LeadStatusHistory history = new LeadStatusHistory();
    history.setLead(savedLead);
    history.setFromStatus(LeadStatus.NEW);
    history.setToStatus(LeadStatus.CONTACTED);
    history.setChangeAt(LocalDateTime.now());
    LeadStatusHistory saved = leadStatusHistoryRepository.save(history);

    saved.setToStatus(LeadStatus.QUALIFIED);
    LeadStatusHistory updated = leadStatusHistoryRepository.save(saved);

    assertThat(updated.getToStatus()).isEqualTo(LeadStatus.QUALIFIED);
  }

  @Test
  void shouldDeleteHistoryEntry() {
    Lead lead = new Lead();
    lead.setTitle("Lead for Delete");
    lead.setStatus(LeadStatus.NEW);
    Lead savedLead = leadRepository.save(lead);

    LeadStatusHistory history = new LeadStatusHistory();
    history.setLead(savedLead);
    history.setFromStatus(LeadStatus.NEW);
    history.setToStatus(LeadStatus.CONTACTED);
    history.setChangeAt(LocalDateTime.now());
    LeadStatusHistory saved = leadStatusHistoryRepository.save(history);
    UUID id = saved.getId();

    leadStatusHistoryRepository.deleteById(id);
    Optional<LeadStatusHistory> found = leadStatusHistoryRepository.findById(id);

    assertThat(found).isEmpty();
  }

  @Test
  void shouldCheckEqualsAndHashCode() {
    Lead lead = new Lead();
    lead.setTitle("Test");
    lead.setStatus(LeadStatus.NEW);
    Lead savedLead = leadRepository.save(lead);

    LeadStatusHistory history1 = new LeadStatusHistory();
    history1.setLead(savedLead);
    history1.setFromStatus(LeadStatus.NEW);
    history1.setToStatus(LeadStatus.CONTACTED);
    history1.setChangeAt(LocalDateTime.now());

    LeadStatusHistory history2 = new LeadStatusHistory();
    history2.setLead(savedLead);
    history2.setFromStatus(LeadStatus.NEW);
    history2.setToStatus(LeadStatus.CONTACTED);
    history2.setChangeAt(LocalDateTime.now());

    LeadStatusHistory saved1 = leadStatusHistoryRepository.save(history1);
    history2.setId(saved1.getId());

    assertThat(history1).isEqualTo(history2);
  }
}
