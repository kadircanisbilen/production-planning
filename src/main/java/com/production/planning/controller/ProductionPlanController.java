package com.production.planning.controller;

import com.production.planning.dto.PartProductionDTO;
import com.production.planning.dto.ProductionPlanDTO;
import com.production.planning.dto.ProductionPlanRequestDTO;
import com.production.planning.entity.ProductionPlan;
import com.production.planning.mapper.ProductionPlanMapper;
import com.production.planning.service.ProductionCalculationService;
import com.production.planning.service.ProductionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/production-plans")
@RequiredArgsConstructor
public class ProductionPlanController {

    private final ProductionPlanService productionPlanService;
    private final ProductionPlanMapper productionPlanMapper;
    private final ProductionCalculationService productionCalculationService;

    @GetMapping
    public ResponseEntity<List<ProductionPlanDTO>> getAllProductionPlans() {
        List<ProductionPlanDTO> dtos = productionPlanService.getAllProductionPlans().stream()
                .map(productionPlanMapper::toProductionPlanDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<ProductionPlanDTO> createProductionPlan(@RequestBody ProductionPlanRequestDTO requestDTO) {
        ProductionPlan plan = productionPlanMapper.toProductionPlan(requestDTO);
        ProductionPlan created = productionPlanService.createProductionPlan(plan);
        return ResponseEntity.ok(productionPlanMapper.toProductionPlanDTO(created));
    }

    @GetMapping("/{id}/calculate-part-production")
    public ResponseEntity<List<PartProductionDTO>> calculatePartProduction(@PathVariable Long id) {
        List<PartProductionDTO> dtos = productionCalculationService.calculatePartProduction(id);
        return ResponseEntity.ok(dtos);
    }
}
