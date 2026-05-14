package ru.mentee.power.crm.leadservice.adapter.out.persistence.repository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.leadservice.adapter.out.persistence.entity.LeadJpaEntity;
import ru.mentee.power.crm.leadservice.adapter.out.persistence.entity.LeadJpaStatus;

@Repository
public interface LeadJpaRepository extends JpaRepository<LeadJpaEntity, UUID> {
  Page<LeadJpaEntity> findByStatus(LeadJpaStatus status, Pageable pageable);

  Page<LeadJpaEntity> findAll(Pageable pageable);
}
