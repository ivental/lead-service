package ru.mentee.power.crm.leadservice.usecase.port.out;

import ru.mentee.power.crm.leadservice.domain.model.LeadStatusHistory;

public interface SaveStatusHistoryPort {
  void save(LeadStatusHistory history);
}
