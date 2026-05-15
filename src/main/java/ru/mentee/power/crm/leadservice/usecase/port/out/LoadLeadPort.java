package ru.mentee.power.crm.leadservice.usecase.port.out;

import java.util.Optional;
import java.util.UUID;
import ru.mentee.power.crm.leadservice.domain.model.Lead;

public interface LoadLeadPort {
  Optional<Lead> findById(UUID id);
}
