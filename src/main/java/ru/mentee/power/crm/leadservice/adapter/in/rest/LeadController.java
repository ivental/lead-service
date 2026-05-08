package ru.mentee.power.crm.leadservice.adapter.in.rest;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mentee.power.crm.leadservice.adapter.mapper.LeadMapper;
import ru.mentee.power.crm.leadservice.domain.model.Lead;
import ru.mentee.power.crm.leadservice.usecase.port.in.ChangeStatusUseCase;
import ru.mentee.power.crm.leadservice.usecase.port.in.CreateLeadUseCase;
import ru.mentee.power.crm.leadservice.usecase.port.in.DeleteLeadUseCase;
import ru.mentee.power.crm.leadservice.usecase.port.in.GetLeadUseCase;
import ru.mentee.power.crm.leadservice.usecase.port.in.ListLeadsByStatusUseCase;
import ru.mentee.power.crm.leadservice.usecase.port.in.UpdateLeadUseCase;

@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
public class LeadController {

  private final CreateLeadUseCase createLeadUseCase;
  private final GetLeadUseCase getLeadUseCase;
  private final UpdateLeadUseCase updateLeadUseCase;
  private final DeleteLeadUseCase deleteLeadUseCase;
  private final ChangeStatusUseCase changeStatusUseCase;
  private final ListLeadsByStatusUseCase listLeadsByStatusUseCase;
  private final LeadMapper leadMapper;

  @PostMapping
  public ResponseEntity<LeadResponse> createLead(@Valid @RequestBody LeadCreateRequest request) {
    Lead lead =
        createLeadUseCase.create(
            request.getTitle(),
            request.getDescription(),
            request.getPersonId(),
            request.getSource());
    LeadResponse response = leadMapper.toResponse(lead);
    return ResponseEntity.created(URI.create("/api/v1/leads/" + lead.getId())).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<LeadResponse> getLeadById(@PathVariable UUID id) {
    return getLeadUseCase
        .getById(id)
        .map(lead -> ResponseEntity.ok(leadMapper.toResponse(lead)))
        .orElse(ResponseEntity.notFound().build());
  }
}
