package ru.mentee.power.crm.leadservice.adapter.out.persistence.adapter;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.mentee.power.crm.leadservice.adapter.mapper.LeadJpaMapper;
import ru.mentee.power.crm.leadservice.adapter.out.persistence.repository.LeadJpaRepository;
import ru.mentee.power.crm.leadservice.adapter.out.persistence.repository.LeadStatusHistoryJpaRepository;
import ru.mentee.power.crm.leadservice.domain.model.Lead;
import ru.mentee.power.crm.leadservice.domain.model.LeadStatus;
import ru.mentee.power.crm.leadservice.domain.model.LeadStatusHistory;
import ru.mentee.power.crm.leadservice.usecase.port.out.DeleteLeadPort;
import ru.mentee.power.crm.leadservice.usecase.port.out.LoadByStatusPort;
import ru.mentee.power.crm.leadservice.usecase.port.out.LoadLeadPort;
import ru.mentee.power.crm.leadservice.usecase.port.out.SaveLeadPort;
import ru.mentee.power.crm.leadservice.usecase.port.out.SaveStatusHistoryPort;

@Component
@RequiredArgsConstructor
public class LeadPersistenceAdapter
    implements SaveLeadPort, LoadLeadPort, LoadByStatusPort, DeleteLeadPort, SaveStatusHistoryPort {

  private final LeadJpaRepository jpaRepository;
  private final LeadStatusHistoryJpaRepository historyJpaRepository;
  private final LeadJpaMapper jpaMapper;

  @Override
  public Lead save(Lead lead) {
    var jpaEntity = jpaMapper.toJpaEntity(lead);
    var savedEntity = jpaRepository.save(jpaEntity);
    return jpaMapper.toDomain(savedEntity);
  }

  @Override
  public Optional<Lead> findById(UUID id) {
    return jpaRepository.findById(id).map(jpaMapper::toDomain);
  }

  @Override
  public void deleteById(UUID id) {
    jpaRepository.deleteById(id);
  }

  @Override
  public void save(LeadStatusHistory history) {
    var jpaEntity = jpaMapper.toHistoryJpaEntity(history);
    historyJpaRepository.save(jpaEntity);
  }

  @Override
  public Page<Lead> findByStatus(LeadStatus status, Pageable pageable) {
    return jpaRepository
        .findByStatus(jpaMapper.toJpaStatus(status), pageable)
        .map(jpaMapper::toDomain);
  }

  @Override
  public Page<Lead> findAll(Pageable pageable) {
    return jpaRepository.findAll(pageable).map(jpaMapper::toDomain);
  }
}
