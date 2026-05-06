package ru.mentee.power.crm.leadservice.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mentee.power.crm.leadservice.domain.LeadStatusHistory;

public interface LeadStatusHistoryRepository extends JpaRepository<LeadStatusHistory, UUID> {}
