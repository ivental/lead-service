package ru.mentee.power.crm.leadservice.adapter.in.rest;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.leadservice.adapter.in.rest.api.LeadApi;
import ru.mentee.power.crm.leadservice.adapter.in.rest.dto.ChangeStatusRequest;
import ru.mentee.power.crm.leadservice.adapter.in.rest.dto.LeadCreateRequest;
import ru.mentee.power.crm.leadservice.adapter.in.rest.dto.LeadResponse;
import ru.mentee.power.crm.leadservice.adapter.in.rest.dto.LeadStatus;
import ru.mentee.power.crm.leadservice.adapter.in.rest.dto.LeadUpdateRequest;
import ru.mentee.power.crm.leadservice.adapter.mapper.LeadMapper;
import ru.mentee.power.crm.leadservice.domain.model.Lead;
import ru.mentee.power.crm.leadservice.usecase.port.in.ChangeStatusUseCase;
import ru.mentee.power.crm.leadservice.usecase.port.in.CreateLeadUseCase;
import ru.mentee.power.crm.leadservice.usecase.port.in.DeleteLeadUseCase;
import ru.mentee.power.crm.leadservice.usecase.port.in.GetLeadUseCase;
import ru.mentee.power.crm.leadservice.usecase.port.in.UpdateLeadUseCase;

@RestController
@RequiredArgsConstructor
public class LeadController implements LeadApi {

  private final CreateLeadUseCase createLeadUseCase;
  private final GetLeadUseCase getLeadUseCase;
  private final UpdateLeadUseCase updateLeadUseCase;
  private final DeleteLeadUseCase deleteLeadUseCase;
  private final ChangeStatusUseCase changeStatusUseCase;
  private final LeadMapper leadMapper;

  @Override
  public ResponseEntity<LeadResponse> createLead(LeadCreateRequest request) {
    Lead lead =
        createLeadUseCase.create(
            request.getTitle(),
            request.getDescription(),
            request.getPersonId(),
            request.getSource());
    LeadResponse response = leadMapper.toResponse(lead);
    return ResponseEntity.created(URI.create("/api/v1/leads/" + lead.getId())).body(response);
  }

  @Override
  public ResponseEntity<LeadResponse> getLeadById(UUID id) {
    return getLeadUseCase
        .getById(id)
        .map(lead -> ResponseEntity.ok(leadMapper.toResponse(lead)))
        .orElse(ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<LeadResponse> updateLead(UUID id, LeadUpdateRequest request) {
    Lead lead = updateLeadUseCase.update(id, request.getTitle(), request.getDescription());
    return ResponseEntity.ok(leadMapper.toResponse(lead));
  }

  @Override
  public ResponseEntity<Void> deleteLead(UUID id) {
    deleteLeadUseCase.delete(id);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<LeadResponse> changeStatus(UUID id, ChangeStatusRequest request) {
    Lead lead =
        changeStatusUseCase.changeStatus(id, leadMapper.toDomainStatus(request.getStatus()));
    return ResponseEntity.ok(leadMapper.toResponse(lead));
  }

  // Будет реализовано позже
  @Override
  public ResponseEntity<List<LeadResponse>> listLeads(LeadStatus status) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }
}
