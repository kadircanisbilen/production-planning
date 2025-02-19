package com.production.planning.service;

import com.production.planning.dto.PartProductionDTO;

import java.util.List;

public interface ProductionCalculationService {
    List<PartProductionDTO> calculatePartProduction(Long productionPlanId);
}
