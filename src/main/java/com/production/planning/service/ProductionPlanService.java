package com.production.planning.service;

import com.production.planning.entity.ProductionPlan;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductionPlanService {
    List<ProductionPlan> getAllProductionPlans();
    ProductionPlan createProductionPlan(ProductionPlan productionPlan);
}
