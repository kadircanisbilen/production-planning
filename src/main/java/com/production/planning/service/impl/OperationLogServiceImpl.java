package com.production.planning.service.impl;

import com.production.planning.entity.BaseEntity;
import com.production.planning.entity.OperationLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.production.planning.repository.OperationLogRepository;
import com.production.planning.service.OperationLogService;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogRepository operationLogRepository;

    @Override
    @Transactional
    public void saveLog(OperationLog log) {
        operationLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OperationLog> getLogsByEntity(String entityName) {
        return operationLogRepository.findByEntityName(entityName);
    }

    @Override
    public List<OperationLog> getAllLogs() {
        return operationLogRepository.findAll();
    }

    @Override
    @Transactional
    public void logOperation(BaseEntity entity, String operationType, String description) {
        OperationLog log = OperationLog.builder()
                .entityId(entity.getId())
                .entityName(entity.getEntityName())
                .operationType(operationType)
                .description(description)
                .timestamp(LocalDateTime.now())
                .build();
        operationLogRepository.save(log);
    }
}
