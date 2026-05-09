package ru.mentee.power.crm.leadservice.adapter.in.rest;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mentee.power.crm.leadservice.adapter.in.rest.api.DefaultApi;
import ru.mentee.power.crm.leadservice.adapter.in.rest.dto.LeadCreateRequest;
import ru.mentee.power.crm.leadservice.adapter.in.rest.dto.LeadResponse;
import ru.mentee.power.crm.leadservice.adapter.in.rest.dto.LeadStatus;
import ru.mentee.power.crm.leadservice.adapter.in.rest.dto.LeadUpdateRequest;
import ru.mentee.power.crm.leadservice.adapter.mapper.LeadMapper;
import ru.mentee.power.crm.leadservice.domain.model.Lead;
import ru.mentee.power.crm.leadservice.usecase.port.in.*;

@RestController
@RequiredArgsConstructor
public class LeadController implements DefaultApi {

  private final CreateLeadUseCase createLeadUseCase;
  private final GetLeadUseCase getLeadUseCase;
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
  public ResponseEntity<LeadResponse> updateLead(UUID id, LeadUpdateRequest leadUpdateRequest) {
    return null;
  }

  // Будет реализовано позже
  @Override
  public ResponseEntity<Void> deleteLead(UUID id) {
    return null;
  }

  // Будет реализовано позже
  @Override
  public ResponseEntity<LeadResponse> changeStatus(UUID id, String body) {
    return null;
  }

  // Будет реализовано позже
  @Override
  public ResponseEntity<List<LeadResponse>> listLeads(LeadStatus status) {
    return null;
  }
}
