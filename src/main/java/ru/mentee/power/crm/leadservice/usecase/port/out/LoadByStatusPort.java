package ru.mentee.power.crm.leadservice.usecase.port.out;

import java.util.List;
import ru.mentee.power.crm.leadservice.domain.model.Lead;
import ru.mentee.power.crm.leadservice.domain.model.LeadStatus;

public interface LoadByStatusPort {
  List<Lead> loadByStatus(LeadStatus status);
}
