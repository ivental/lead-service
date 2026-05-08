package ru.mentee.power.crm.leadservice.usecase.port.out;

import ru.mentee.power.crm.leadservice.domain.model.Lead;

public interface SaveLeadPort {
  Lead save(Lead lead);
}
