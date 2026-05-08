package ru.mentee.power.crm.leadservice.usecase.port.in;

import java.util.UUID;

public interface DeleteLeadUseCase {
  void delete(UUID id);
}
