package ru.mentee.power.crm.leadservice.adapter.out.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.mentee.power.crm.leadservice.adapter.out.client.api.PersonsApi;

@FeignClient(name = "contact-service", url = "${app.contact-service.url:http://localhost:8083}")
public interface ContactServiceFeignClient extends PersonsApi {}
