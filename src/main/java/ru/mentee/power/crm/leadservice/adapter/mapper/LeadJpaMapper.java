package ru.mentee.power.crm.leadservice.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.mentee.power.crm.leadservice.adapter.out.persistence.entity.LeadJpaEntity;
import ru.mentee.power.crm.leadservice.adapter.out.persistence.entity.LeadJpaStatus;
import ru.mentee.power.crm.leadservice.adapter.out.persistence.entity.LeadStatusHistoryJpaEntity;
import ru.mentee.power.crm.leadservice.domain.model.Lead;
import ru.mentee.power.crm.leadservice.domain.model.LeadStatus;
import ru.mentee.power.crm.leadservice.domain.model.LeadStatusHistory;

@Mapper(componentModel = "spring")
public interface LeadJpaMapper {

  @Mapping(target = "status", expression = "java(toJpaStatus(lead.getStatus()))")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  LeadJpaEntity toJpaEntity(Lead lead);

  @Mapping(target = "status", expression = "java(toDomainStatus(entity.getStatus()))")
  Lead toDomain(LeadJpaEntity entity);

  default LeadJpaStatus toJpaStatus(LeadStatus status) {
    if (status == null) {
      return null;
    }
    return LeadJpaStatus.valueOf(status.name());
  }

  default LeadStatus toDomainStatus(LeadJpaStatus jpaStatus) {
    if (jpaStatus == null) {
      return null;
    }
    return LeadStatus.valueOf(jpaStatus.name());
  }

  default LeadStatusHistoryJpaEntity toHistoryJpaEntity(LeadStatusHistory history) {
    if (history == null) {
      return null;
    }
    LeadStatusHistoryJpaEntity entity = new LeadStatusHistoryJpaEntity();
    entity.setLeadId(history.getLeadId());
    entity.setFromStatus(toJpaStatus(history.getFromStatus()));
    entity.setToStatus(toJpaStatus(history.getToStatus()));
    entity.setChangedAt(history.getChangedAt());
    entity.setVersion(0L);
    return entity;
  }
}
