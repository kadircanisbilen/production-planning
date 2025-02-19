package com.production.planning.controller;

import com.production.planning.entity.OperationLog;
import com.production.planning.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

    @GetMapping
    public ResponseEntity<List<OperationLog>> getAllLogs() {
        return ResponseEntity.ok(operationLogService.getAllLogs());
    }

    @GetMapping("/{entityName}")
    public ResponseEntity<List<OperationLog>> getLogsByEntity(@PathVariable String entityName) {
        return ResponseEntity.ok(operationLogService.getLogsByEntity(entityName));
    }
}
