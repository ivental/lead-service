package ru.mentee.power.crm.leadservice.usecase.port.in;

import java.util.UUID;
import ru.mentee.power.crm.leadservice.domain.model.Lead;

public interface GetLeadUseCase {
  Lead getById(UUID id);
}
