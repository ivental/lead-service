package ru.mentee.power.crm.leadservice.usecase.service;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.leadservice.domain.exception.LeadNotFoundException;
import ru.mentee.power.crm.leadservice.domain.model.Lead;
import ru.mentee.power.crm.leadservice.domain.model.LeadStatus;
import ru.mentee.power.crm.leadservice.domain.model.LeadStatusHistory;
import ru.mentee.power.crm.leadservice.usecase.port.in.ChangeStatusUseCase;
import ru.mentee.power.crm.leadservice.usecase.port.in.CreateLeadUseCase;
import ru.mentee.power.crm.leadservice.usecase.port.in.DeleteLeadUseCase;
import ru.mentee.power.crm.leadservice.usecase.port.in.GetLeadUseCase;
import ru.mentee.power.crm.leadservice.usecase.port.in.ListLeadsByStatusUseCase;
import ru.mentee.power.crm.leadservice.usecase.port.in.UpdateLeadUseCase;
import ru.mentee.power.crm.leadservice.usecase.port.out.ContactClientPort;
import ru.mentee.power.crm.leadservice.usecase.port.out.DeleteLeadPort;
import ru.mentee.power.crm.leadservice.usecase.port.out.LoadByStatusPort;
import ru.mentee.power.crm.leadservice.usecase.port.out.LoadLeadPort;
import ru.mentee.power.crm.leadservice.usecase.port.out.SaveLeadPort;
import ru.mentee.power.crm.leadservice.usecase.port.out.SaveStatusHistoryPort;

@Service
@RequiredArgsConstructor
public class LeadService
    implements CreateLeadUseCase,
        GetLeadUseCase,
        UpdateLeadUseCase,
        DeleteLeadUseCase,
        ChangeStatusUseCase,
        ListLeadsByStatusUseCase {

  private final SaveLeadPort saveLeadPort;
  private final LoadLeadPort loadLeadPort;
  private final LoadByStatusPort loadByStatusPort;
  private final DeleteLeadPort deleteLeadPort;
  private final SaveStatusHistoryPort saveStatusHistoryPort;
  private final ContactClientPort contactClientPort;

  @Override
  @Transactional
  public Lead create(
      String title,
      String description,
      UUID personId,
      String source,
      String email,
      String fullName) {
    UUID resolvedPersonId = resolvePersonId(personId, email, fullName);
    Lead lead = Lead.createNew(title, description, resolvedPersonId, source);
    return saveLeadPort.save(lead);
  }

  private UUID resolvePersonId(UUID personId, String email, String fullName) {
    if (personId != null) {
      return personId;
    }
    if (email != null && !email.isBlank()) {
      return contactClientPort
          .findPersonIdByEmail(email)
          .orElseGet(() -> contactClientPort.createPerson(email, fullName));
    }
    throw new IllegalArgumentException("Either personId or email is required");
  }

  @Override
  public Lead getById(UUID id) {
    return loadLeadPort.findById(id).orElseThrow(() -> new LeadNotFoundException(id));
  }

  @Override
  public Page<Lead> listByStatus(LeadStatus status, Pageable pageable) {
    if (status == null) {
      return loadByStatusPort.findAll(pageable);
    }
    return loadByStatusPort.findByStatus(status, pageable);
  }

  @Override
  @Transactional
  public Lead update(UUID id, String title, String description) {
    Lead lead = loadLeadPort.findById(id).orElseThrow(() -> new LeadNotFoundException(id));
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("Title is required");
    }
    lead.setTitle(title);

    if (description != null) {
      lead.setDescription(description);
    }
    lead.setUpdatedAt(LocalDateTime.now());
    return saveLeadPort.save(lead);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    loadLeadPort.findById(id).orElseThrow(() -> new LeadNotFoundException(id));
    deleteLeadPort.deleteById(id);
  }

  @Override
  @Transactional
  public Lead changeStatus(UUID id, LeadStatus newStatus) {
    if (newStatus == null) {
      throw new IllegalArgumentException("Status is required");
    }
    Lead lead = loadLeadPort.findById(id).orElseThrow(() -> new LeadNotFoundException(id));
    LeadStatus fromStatus = lead.getStatus();
    lead.changeStatus(newStatus);
    Lead savedLead = saveLeadPort.save(lead);
    LeadStatusHistory history = new LeadStatusHistory();
    history.setLeadId(id);
    history.setFromStatus(fromStatus);
    history.setToStatus(newStatus);
    history.setChangedAt(java.time.LocalDateTime.now());
    saveStatusHistoryPort.save(history);
    return savedLead;
  }
}
