package com.production.planning.service.impl;

import com.production.planning.annotation.LogOperation;
import com.production.planning.entity.ProductionPlan;
import com.production.planning.entity.Project;
import com.production.planning.repository.ProductionPlanRepository;
import com.production.planning.repository.ProjectRepository;
import com.production.planning.service.ProductionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductionPlanServiceImpl implements ProductionPlanService {

    private final ProductionPlanRepository productionPlanRepository;
    private final ProjectRepository projectRepository;

    @Override
    public List<ProductionPlan> getAllProductionPlans() {
        return productionPlanRepository.findAll();
    }

    @Override
    @Transactional
    @LogOperation(operationType = "CREATE")
    public ProductionPlan createProductionPlan(ProductionPlan productionPlan) {
        Project project = projectRepository.findById(productionPlan.getProject().getId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if ((productionPlan.getMonth() == null || productionPlan.getMonth().isBlank()) &&
                (productionPlan.getWeek() == null || productionPlan.getWeek().isBlank())) {
            throw new RuntimeException("Either month or week must be provided for the production plan.");
        }

        if (productionPlan.getWeek() != null && !productionPlan.getWeek().isBlank()) {
            productionPlan.setMonth(null);
        } else {
            productionPlan.setWeek(null);
        }

        productionPlan.setProject(project);
        return productionPlanRepository.save(productionPlan);
    }

}
