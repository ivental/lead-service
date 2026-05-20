package ru.mentee.power.crm.leadservice.adapter.out.client;

import feign.FeignException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.mentee.power.crm.leadservice.adapter.out.client.dto.CreatePersonRequest;
import ru.mentee.power.crm.leadservice.adapter.out.client.dto.ListPersons200Response;
import ru.mentee.power.crm.leadservice.adapter.out.client.dto.PersonResponse;
import ru.mentee.power.crm.leadservice.usecase.port.out.ContactClientPort;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContactServiceAdapter implements ContactClientPort {

  private final ContactServiceFeignClient contactServiceClient;

  @Override
  public Optional<UUID> findPersonIdByEmail(String email) {
    try {
      ResponseEntity<ListPersons200Response> response =
          contactServiceClient.listPersons(email, 0, 1);
      ListPersons200Response body = response.getBody();

      if (body == null || body.getContent() == null || body.getContent().isEmpty()) {
        return Optional.empty();
      }

      return body.getContent().stream()
          .filter(p -> email.equalsIgnoreCase(p.getEmail()))
          .map(PersonResponse::getId)
          .findFirst();

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

    ResponseEntity<PersonResponse> response = contactServiceClient.createPerson(request);
    PersonResponse body = response.getBody();

    if (body == null) {
      throw new RuntimeException("Response body is null");
    }

    return body.getId();
  }
}
