package ru.mentee.power.crm.leadservice.usecase.port.out;

import java.util.UUID;

public interface DeleteLeadPort {
  void deleteById(UUID id);
}
