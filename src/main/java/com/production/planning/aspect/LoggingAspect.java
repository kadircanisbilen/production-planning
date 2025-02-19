package com.production.planning.aspect;

import com.production.planning.annotation.LogOperation;
import com.production.planning.entity.BaseEntity;
import com.production.planning.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final OperationLogService operationLogService;

    @AfterReturning(value = "@annotation(logOperation)", returning = "result")
    public void logAfter(JoinPoint joinPoint, LogOperation logOperation, Object result) {
        if (result instanceof BaseEntity entity) {
            String operationType = logOperation.operationType();
            String description = "Operation " + operationType + " performed on " + entity.getEntityName();

            operationLogService.logOperation(entity, operationType, description);
        }
    }
}


