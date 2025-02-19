package com.production.planning.service.impl;

import com.production.planning.annotation.LogOperation;
import com.production.planning.dto.ModelPercentageDTO;
import com.production.planning.dto.ProjectUpdateDTO;
import com.production.planning.entity.Model;
import com.production.planning.entity.ProductionPlan;
import com.production.planning.entity.ProductionPlanDetail;
import com.production.planning.entity.Project;
import com.production.planning.enumeration.PlanningType;
import com.production.planning.repository.ProductionPlanDetailRepository;
import com.production.planning.repository.ProductionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.production.planning.repository.ProjectRepository;
import com.production.planning.service.ProjectService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProductionPlanRepository productionPlanRepository;
    private final ProductionPlanDetailRepository productionPlanDetailRepository;

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    @Transactional
    @LogOperation(operationType = "CREATE")
    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    @Override
    @Transactional
    @LogOperation(operationType = "SOFT_DELETE")
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        project.setActive(false);
        projectRepository.save(project);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @LogOperation(operationType = "UPDATE")
    public Project updateProjectSettings(Long projectId, ProjectUpdateDTO updateDTO) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        String previousPlanningType = project.getPlanningType().trim().toLowerCase();
        String newPlanningType = updateDTO.getPlanningType().trim().toLowerCase();

        // Convert DTO model percentages into a map for easy lookup
        Map<Long, Map<String, Double>> updatedModelPercentages = updateDTO.getModelPercentages().stream()
                .collect(Collectors.groupingBy(
                        ModelPercentageDTO::getModelId,
                        Collectors.toMap(ModelPercentageDTO::getMonth, ModelPercentageDTO::getPercentage)
                ));

        // Fetch existing production plans
        List<ProductionPlan> productionPlans = productionPlanRepository.findByProjectId(projectId);

        if (!productionPlans.isEmpty()) {
            // Store previous percentage values before updating
            Map<Long, Map<String, Double>> previousPercentages = productionPlanDetailRepository
                    .findAllByProductionPlanIdIn(
                            productionPlans.stream().map(ProductionPlan::getId).collect(Collectors.toList())
                    ).stream()
                    .collect(Collectors.groupingBy(
                            detail -> detail.getModel().getId(),
                            Collectors.toMap(
                                    detail -> detail.getProductionPlan().getMonth(),
                                    ProductionPlanDetail::getPercentage
                            )
                    ));

            // Update percentage values in existing details
            List<ProductionPlanDetail> productionPlanDetails = productionPlanDetailRepository.findAllByProductionPlanIdIn(
                    productionPlans.stream().map(ProductionPlan::getId).collect(Collectors.toList()));

            for (ProductionPlanDetail detail : productionPlanDetails) {
                if (updatedModelPercentages.containsKey(detail.getModel().getId())) {
                    Map<String, Double> monthPercentageMap = updatedModelPercentages.get(detail.getModel().getId());
                    if (monthPercentageMap.containsKey(detail.getProductionPlan().getMonth())) {
                        detail.setPercentage(monthPercentageMap.get(detail.getProductionPlan().getMonth()));
                    }
                }
            }
            productionPlanDetailRepository.saveAll(productionPlanDetails);

            if (!previousPlanningType.equalsIgnoreCase(newPlanningType)) {
                previousPercentages.forEach((modelId, monthPercentageMap) -> {
                    monthPercentageMap.forEach((month, percentage) -> {
                        productionPlanDetailRepository.updatePercentageByModelIdAndMonth(modelId, month, percentage);
                    });
                });
            }
        } else {
            throw new RuntimeException("No production plans found for projectId: " + projectId);
        }

        if (!previousPlanningType.equalsIgnoreCase(newPlanningType)) {
            project.setPlanningType(newPlanningType);

            if (PlanningType.MONTHLY.toString().equalsIgnoreCase(newPlanningType) ||
                    PlanningType.WEEKLY.toString().equalsIgnoreCase(newPlanningType)) {
                project.getModels().forEach(Model::activate);
            }
        }
        return projectRepository.save(project);
    }
}
