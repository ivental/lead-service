package ru.mentee.power.crm.leadservice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertySource;
import ru.mentee.power.crm.leadservice.domain.Lead;
import ru.mentee.power.crm.leadservice.domain.LeadStatus;

@DataJpaTest
class LeadRepositoryTest {

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

  @Autowired private LeadRepository leadRepository;

  @Test
  void shouldSaveAndFindLeadById() {
    Lead lead = new Lead();
    lead.setTitle("Test Lead");
    lead.setStatus(LeadStatus.NEW);

    Lead saved = leadRepository.save(lead);
    Optional<Lead> found = leadRepository.findById(saved.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getTitle()).isEqualTo("Test Lead");
    assertThat(found.get().getStatus()).isEqualTo(LeadStatus.NEW);
  }

  @Test
  void shouldFindLeadsByStatus() {
    Lead lead1 = new Lead();
    lead1.setTitle("Lead 1");
    lead1.setStatus(LeadStatus.NEW);
    leadRepository.save(lead1);

    Lead lead2 = new Lead();
    lead2.setTitle("Lead 2");
    lead2.setStatus(LeadStatus.NEW);
    leadRepository.save(lead2);

    Lead lead3 = new Lead();
    lead3.setTitle("Lead 3");
    lead3.setStatus(LeadStatus.CONTACTED);
    leadRepository.save(lead3);

    Pageable pageable = PageRequest.of(0, 10);
    Page<Lead> newLeads = leadRepository.findByStatus(LeadStatus.NEW, pageable);

    assertThat(newLeads.getContent()).hasSize(2);
    assertThat(newLeads.getContent()).allMatch(lead -> lead.getStatus() == LeadStatus.NEW);
  }

  @Test
  void shouldReturnEmptyPageWhenNoLeadsWithStatus() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Lead> qualifiedLeads = leadRepository.findByStatus(LeadStatus.QUALIFIED, pageable);

    assertThat(qualifiedLeads.getContent()).isEmpty();
    assertThat(qualifiedLeads.getTotalElements()).isZero();
  }

  @Test
  void shouldUpdateLead() {
    Lead lead = new Lead();
    lead.setTitle("Original Title");
    lead.setStatus(LeadStatus.NEW);
    Lead saved = leadRepository.save(lead);

    saved.setTitle("Updated Title");
    saved.setStatus(LeadStatus.CONTACTED);
    Lead updated = leadRepository.save(saved);

    assertThat(updated.getTitle()).isEqualTo("Updated Title");
    assertThat(updated.getStatus()).isEqualTo(LeadStatus.CONTACTED);
  }

  @Test
  void shouldDeleteLead() {
    Lead lead = new Lead();
    lead.setTitle("To Delete");
    lead.setStatus(LeadStatus.NEW);
    Lead saved = leadRepository.save(lead);
    UUID id = saved.getId();

    leadRepository.deleteById(id);
    Optional<Lead> found = leadRepository.findById(id);

    assertThat(found).isEmpty();
  }

  @Test
  void shouldSaveLeadWithPersonId() {
    Lead lead = new Lead();
    lead.setTitle("Lead with Person");
    lead.setStatus(LeadStatus.NEW);
    lead.setPersonId(UUID.randomUUID());

    Lead saved = leadRepository.save(lead);
    Optional<Lead> found = leadRepository.findById(saved.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getPersonId()).isNotNull();
  }

  @Test
  void shouldSaveLeadWithSource() {
    Lead lead = new Lead();
    lead.setTitle("Lead with Source");
    lead.setStatus(LeadStatus.NEW);
    lead.setSource("website");

    Lead saved = leadRepository.save(lead);
    Optional<Lead> found = leadRepository.findById(saved.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getSource()).isEqualTo("website");
  }
}
