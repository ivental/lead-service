package ru.mentee.power.crm.leadservice.adapter.out.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mentee.power.crm.leadservice.adapter.out.client.dto.CreatePersonRequest;
import ru.mentee.power.crm.leadservice.adapter.out.client.dto.PersonResponse;

@FeignClient(name = "contact-service", url = "${app.contact-service.url:http://localhost:8083}")
public interface ContactServiceClient {

  @GetMapping("/api/v1/persons")
  PersonResponse findByEmail(@RequestParam("email") String email);

  @PostMapping("/api/v1/persons")
  PersonResponse createPerson(@RequestBody CreatePersonRequest request);
}
