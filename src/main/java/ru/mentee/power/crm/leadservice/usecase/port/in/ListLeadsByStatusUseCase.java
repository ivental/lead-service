package ru.mentee.power.crm.leadservice.usecase.port.in;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.mentee.power.crm.leadservice.domain.model.Lead;
import ru.mentee.power.crm.leadservice.domain.model.LeadStatus;

public interface ListLeadsByStatusUseCase {
  Page<Lead> listByStatus(LeadStatus status, Pageable pageable);
}
