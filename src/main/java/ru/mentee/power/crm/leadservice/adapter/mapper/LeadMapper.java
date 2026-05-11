package ru.mentee.power.crm.leadservice.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.mentee.power.crm.leadservice.adapter.in.rest.dto.LeadResponse;
import ru.mentee.power.crm.leadservice.domain.model.Lead;
import ru.mentee.power.crm.leadservice.domain.model.LeadStatus;

@Mapper(componentModel = "spring")
public interface LeadMapper {

  @Mapping(target = "status", expression = "java(lead.getStatus().name())")
  LeadResponse toResponse(Lead lead);

  default LeadStatus map(String value) {
    if (value == null) {
      return null;
    }
    return LeadStatus.valueOf(value);
  }

  default ru.mentee.power.crm.leadservice.domain.model.LeadStatus toDomainStatus(
      ru.mentee.power.crm.leadservice.adapter.in.rest.dto.LeadStatus dtoStatus) {
    if (dtoStatus == null) return null;
    return ru.mentee.power.crm.leadservice.domain.model.LeadStatus.valueOf(dtoStatus.name());
  }
}
