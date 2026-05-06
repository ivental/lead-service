package ru.mentee.power.crm.leadservice.repository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mentee.power.crm.leadservice.domain.Lead;
import ru.mentee.power.crm.leadservice.domain.LeadStatus;

public interface LeadRepository extends JpaRepository<Lead, UUID> {

  Page<Lead> findByStatus(LeadStatus status, Pageable pageable);
}
