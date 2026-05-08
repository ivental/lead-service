package ru.mentee.power.crm.leadservice.usecase.port.in;

import java.util.UUID;
import ru.mentee.power.crm.leadservice.domain.model.Lead;

public interface UpdateLeadUseCase {
  Lead update(UUID id, String title, String description);
}
