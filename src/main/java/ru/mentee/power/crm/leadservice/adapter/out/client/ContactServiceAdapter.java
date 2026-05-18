package ru.mentee.power.crm.leadservice.adapter.out.client;

import feign.FeignException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.mentee.power.crm.leadservice.adapter.out.client.dto.CreatePersonRequest;
import ru.mentee.power.crm.leadservice.adapter.out.client.dto.PersonResponse;
import ru.mentee.power.crm.leadservice.usecase.port.out.ContactClientPort;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContactServiceAdapter implements ContactClientPort {

  private final ContactServiceClient contactServiceClient;

  @Override
  public Optional<UUID> findPersonIdByEmail(String email) {
    try {
      PersonResponse response = contactServiceClient.findByEmail(email);
      return response != null ? Optional.of(response.getId()) : Optional.empty();
    } catch (FeignException.NotFound e) {
      log.debug("Person with email {} not found", email);
      return Optional.empty();
    } catch (FeignException e) {
      log.error("Error calling contact service for email {}: {}", email, e.getMessage());
      throw e;
    }
  }

  @Override
  public UUID createPerson(String email, String fullName) {
    CreatePersonRequest request = new CreatePersonRequest();
    request.setEmail(email);
    request.setFullName(fullName);
    request.setPhone(null);

    PersonResponse response = contactServiceClient.createPerson(request);
    return response.getId();
  }
}
