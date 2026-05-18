package ru.mentee.power.crm.leadservice.usecase.port.in;

import java.util.UUID;
import ru.mentee.power.crm.leadservice.domain.model.Lead;

public interface CreateLeadUseCase {
  Lead create(
      String title,
      String description,
      UUID personId,
      String source,
      String email,
      String fullName);
}
