package com.production.planning.service;

import com.production.planning.entity.BaseEntity;
import com.production.planning.entity.OperationLog;

import java.util.List;

public interface OperationLogService {
    void saveLog(OperationLog log);
    List<OperationLog> getLogsByEntity(String entityName);
    List<OperationLog> getAllLogs();
    void logOperation(BaseEntity entity, String operationType, String description);
}

