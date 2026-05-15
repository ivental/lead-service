package ru.mentee.power.crm.leadservice.usecase.port.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.mentee.power.crm.leadservice.domain.model.Lead;
import ru.mentee.power.crm.leadservice.domain.model.LeadStatus;

public interface LoadByStatusPort {
  Page<Lead> findByStatus(LeadStatus status, Pageable pageable);

  Page<Lead> findAll(Pageable pageable);
}
