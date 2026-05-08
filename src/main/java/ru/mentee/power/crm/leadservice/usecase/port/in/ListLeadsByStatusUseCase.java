package ru.mentee.power.crm.leadservice.usecase.port.in;

import java.util.List;
import ru.mentee.power.crm.leadservice.domain.model.Lead;
import ru.mentee.power.crm.leadservice.domain.model.LeadStatus;

public interface ListLeadsByStatusUseCase {
  List<Lead> listByStatus(LeadStatus status);
}
