package ru.mentee.power.crm.leadservice.usecase.port.in;

import java.util.Optional;
import java.util.UUID;
import ru.mentee.power.crm.leadservice.domain.model.Lead;

public interface GetLeadUseCase {
  Optional<Lead> getById(UUID id);
}
