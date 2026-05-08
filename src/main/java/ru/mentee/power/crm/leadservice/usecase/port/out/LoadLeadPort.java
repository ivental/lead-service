package ru.mentee.power.crm.leadservice.usecase.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ru.mentee.power.crm.leadservice.domain.model.Lead;

public interface LoadLeadPort {
  Optional<Lead> loadById(UUID id);

  List<Lead> loadAll();
}
