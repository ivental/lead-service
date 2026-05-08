package ru.mentee.power.crm.leadservice.adapter.out.persistence.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.leadservice.adapter.out.persistence.entity.LeadJpaEntity;
import ru.mentee.power.crm.leadservice.adapter.out.persistence.entity.LeadJpaStatus;

@Repository
public interface LeadJpaRepository extends JpaRepository<LeadJpaEntity, UUID> {
  List<LeadJpaEntity> findByStatus(LeadJpaStatus status);
}
