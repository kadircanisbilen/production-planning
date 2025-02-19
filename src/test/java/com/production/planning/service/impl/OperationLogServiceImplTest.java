package com.production.planning.service.impl;

import com.production.planning.entity.BaseEntity;
import com.production.planning.entity.OperationLog;
import com.production.planning.repository.OperationLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OperationLogServiceImplTest {

    @Mock
    private OperationLogRepository operationLogRepository;

    @InjectMocks
    private OperationLogServiceImpl operationLogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveLog() {
        // Given
        OperationLog log = new OperationLog();
        log.setEntityId(1L);
        log.setEntityName("TestEntity");
        log.setOperationType("CREATE");
        log.setDescription("Entity created");
        log.setTimestamp(LocalDateTime.now());

        // When
        operationLogService.saveLog(log);

        // Then
        verify(operationLogRepository, times(1)).save(log);
    }

    @Test
    void testGetLogsByEntity() {
        // Given
        String entityName = "TestEntity";
        OperationLog log1 = new OperationLog();
        log1.setEntityId(1L);
        log1.setEntityName(entityName);
        log1.setOperationType("CREATE");
        log1.setDescription("Entity created");
        log1.setTimestamp(LocalDateTime.now());

        OperationLog log2 = new OperationLog();
        log2.setEntityId(2L);
        log2.setEntityName(entityName);
        log2.setOperationType("UPDATE");
        log2.setDescription("Entity updated");
        log2.setTimestamp(LocalDateTime.now());

        List<OperationLog> logs = Arrays.asList(log1, log2);
        when(operationLogRepository.findByEntityName(entityName)).thenReturn(logs);

        // When
        List<OperationLog> result = operationLogService.getLogsByEntity(entityName);

        // Then
        assertEquals(2, result.size());
        verify(operationLogRepository, times(1)).findByEntityName(entityName);
    }

    @Test
    void testGetAllLogs() {
        // Given
        OperationLog log1 = new OperationLog();
        log1.setEntityId(1L);
        log1.setEntityName("TestEntity1");
        log1.setOperationType("CREATE");
        log1.setDescription("Entity created");
        log1.setTimestamp(LocalDateTime.now());

        OperationLog log2 = new OperationLog();
        log2.setEntityId(2L);
        log2.setEntityName("TestEntity2");
        log2.setOperationType("UPDATE");
        log2.setDescription("Entity updated");
        log2.setTimestamp(LocalDateTime.now());

        List<OperationLog> logs = Arrays.asList(log1, log2);
        when(operationLogRepository.findAll()).thenReturn(logs);

        // When
        List<OperationLog> result = operationLogService.getAllLogs();

        // Then
        assertEquals(2, result.size());
        verify(operationLogRepository, times(1)).findAll();
    }

    @Test
    void testLogOperation() {
        // Given
        BaseEntity entity = new BaseEntity() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public String getEntityName() {
                return "TestEntity";
            }
        };
        String operationType = "CREATE";
        String description = "Entity created";

        // When
        operationLogService.logOperation(entity, operationType, description);

        // Then
        verify(operationLogRepository, times(1)).save(any(OperationLog.class));
    }
}