package ru.mentee.power.crm.leadservice.usecase.port.out;

import java.util.Optional;
import java.util.UUID;

public interface ContactClientPort {
  Optional<UUID> findPersonIdByEmail(String email);

  UUID createPerson(String email, String fullName);
}
