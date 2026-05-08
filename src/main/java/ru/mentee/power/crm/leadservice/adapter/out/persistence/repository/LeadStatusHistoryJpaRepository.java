package ru.mentee.power.crm.leadservice.adapter.out.persistence.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.leadservice.adapter.out.persistence.entity.LeadStatusHistoryJpaEntity;

@Repository
public interface LeadStatusHistoryJpaRepository
    extends JpaRepository<LeadStatusHistoryJpaEntity, UUID> {}
