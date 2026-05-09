package ru.mentee.power.crm.leadservice.usecase.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

  @Override
  public Lead create(String title, String description, UUID personId, String source) {
    if (personId == null) {
      throw new IllegalArgumentException("personId is required");
    }
    Lead lead = Lead.createNew(title, description, personId, source);
    return saveLeadPort.save(lead);
  }

  @Override
  public Optional<Lead> getById(UUID id) {
    return loadLeadPort.loadById(id);
  }

  @Override
  public List<Lead> listByStatus(LeadStatus status) {
    if (status == null) {
      throw new IllegalArgumentException("Status parameter is required");
    }
    return loadByStatusPort.loadByStatus(status);
  }

  @Override
  public Lead update(UUID id, String title, String description) {
    Lead lead = loadLeadPort.loadById(id).orElseThrow(() -> new LeadNotFoundException(id));
    if (title != null) {
      lead.setTitle(title);
    }
    if (description != null) {
      lead.setDescription(description);
    }
    return saveLeadPort.save(lead);
  }

  @Override
  public void delete(UUID id) {
    loadLeadPort.loadById(id).orElseThrow(() -> new LeadNotFoundException(id));
    deleteLeadPort.deleteById(id);
  }

  @Override
  @Transactional
  public Lead changeStatus(UUID id, LeadStatus newStatus) {
    if (newStatus == null) {
      throw new IllegalArgumentException("Status is required");
    }
    Lead lead = loadLeadPort.loadById(id).orElseThrow(() -> new LeadNotFoundException(id));
    LeadStatus fromStatus = lead.getStatus();
    lead.changeStatus(newStatus);
    Lead savedLead = saveLeadPort.save(lead);
    LeadStatusHistory history = new LeadStatusHistory();
    history.setId(UUID.randomUUID());
    history.setLeadId(id);
    history.setFromStatus(fromStatus);
    history.setToStatus(newStatus);
    history.setChangedAt(java.time.LocalDateTime.now());
    saveStatusHistoryPort.save(history);
    return savedLead;
  }
}
