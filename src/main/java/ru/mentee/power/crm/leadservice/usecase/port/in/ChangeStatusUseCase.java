package ru.mentee.power.crm.leadservice.usecase.port.in;

import java.util.UUID;
import ru.mentee.power.crm.leadservice.domain.model.Lead;
import ru.mentee.power.crm.leadservice.domain.model.LeadStatus;

public interface ChangeStatusUseCase {
  Lead changeStatus(UUID id, LeadStatus newStatus);
}
