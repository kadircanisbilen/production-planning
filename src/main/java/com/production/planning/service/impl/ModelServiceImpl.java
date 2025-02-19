package com.production.planning.service.impl;

import com.production.planning.annotation.LogOperation;
import com.production.planning.dto.ModelPercentageDTO;
import com.production.planning.entity.Model;
import com.production.planning.entity.ModelPart;
import com.production.planning.entity.Part;
import com.production.planning.entity.ProductionPlan;
import com.production.planning.entity.ProductionPlanDetail;
import com.production.planning.entity.Project;
import com.production.planning.enumeration.PlanningType;
import com.production.planning.repository.ModelPartRepository;
import com.production.planning.repository.ModelRepository;
import com.production.planning.repository.PartRepository;
import com.production.planning.repository.ProductionPlanDetailRepository;
import com.production.planning.repository.ProductionPlanRepository;
import com.production.planning.repository.ProjectRepository;
import com.production.planning.service.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    private final ModelRepository modelRepository;
    private final ProductionPlanDetailRepository productionPlanDetailRepository;
    private final ProductionPlanRepository productionPlanRepository;
    private final ProjectRepository projectRepository;
    private final ModelPartRepository modelPartRepository;
    private final PartRepository partRepository;

    @Override
    public List<Model> getAllModels() {
        return modelRepository.findAllActiveModels();
    }

    @Override
    public Model getModelById(Long id) {
        return modelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Model not found"));
    }

    @Override
    @Transactional
    @LogOperation(operationType = "CREATE")
    public Model createModel(Model model, Map<Long, Integer> parts) {
        Project project = projectRepository.findById(model.getProject().getId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Model savedModel = modelRepository.save(
                Model.builder()
                        .name(model.getName())
                        .project(project)
                        .active(true)
                        .build()
        );

        if (parts != null && !parts.isEmpty()) {
            List<ModelPart> modelParts = parts.entrySet().stream()
                    .map(entry -> {
                        Part part = partRepository.findById(entry.getKey())
                                .orElseThrow(() -> new RuntimeException("Part not found"));
                        return ModelPart.builder()
                                .model(savedModel)
                                .part(part)
                                .quantity(entry.getValue())
                                .build();
                    })
                    .collect(Collectors.toList());

            modelPartRepository.saveAll(modelParts);
            savedModel.setModelParts(modelParts);
        }

        return savedModel;
    }

    @Override
    @Transactional
    @LogOperation(operationType = "UPDATE")
    public Model updateModel(Long id, Model model) {
        Model existing = getModelById(id);

        if (!existing.getActive()) {
            throw new RuntimeException("This model has been deleted and cannot be modified.");
        }

        existing.setName(model.getName());
        return modelRepository.save(existing);
    }

    @Override
    @Transactional
    @LogOperation(operationType = "SOFT_DELETE")
    public void deleteModel(Long id) {
        Model model = modelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Model not found"));
        model.deactivate();
        modelRepository.save(model);
    }

    @Override
    @Transactional
    @LogOperation(operationType = "UPDATE")
    public void updateModelPercentage(Long modelId, double percentage) {
        Model model = modelRepository.findById(modelId)
                .orElseThrow(() -> new RuntimeException("Model not found"));

        if (!model.getActive()) {
            throw new RuntimeException("This model has been deleted and cannot be modified.");
        }

        List<ProductionPlanDetail> details = productionPlanDetailRepository.findByModelId(modelId);

        for (ProductionPlanDetail detail : details) {
            detail.setPercentage(percentage);
            productionPlanDetailRepository.save(detail);
        }
    }

    @Override
    @Transactional
    @LogOperation(operationType = "UPDATE")
    public void updateModelStatus(Long modelId, boolean active) {
        Model model = modelRepository.findById(modelId)
                .orElseThrow(() -> new RuntimeException("Model not found"));

        Project project = model.getProject();
        String planningType = project.getPlanningType().toLowerCase();

        if ((PlanningType.MONTHLY.toString().equalsIgnoreCase(planningType) || PlanningType.WEEKLY.toString().equalsIgnoreCase(planningType)) && !active) {
            throw new RuntimeException("Models cannot be deactivated in 'monthly' or 'weekly' planning projects.");
        }

        // Only update if there's a change
        if (!model.getActive().equals(active)) {
            if (active) {
                model.activate();
            } else {
                model.deactivate();
            }
            modelRepository.save(model);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelPercentageDTO> getModelPercentagesByDateRange(Long projectId, String startDate, String endDate) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!"monthly".equalsIgnoreCase(project.getPlanningType())) {
            throw new RuntimeException("Model percentages can only be retrieved for 'monthly' planning projects.");
        }

        List<ProductionPlanDetail> details = productionPlanDetailRepository.findByProjectIdAndDateRange(projectId, startDate, endDate);

        return details.stream()
                .map(detail -> new ModelPercentageDTO(detail.getModel().getId(), detail.getProductionPlan().getMonth(), detail.getProductionPlan().getWeek(), detail.getPercentage()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelPercentageDTO> getSortedModelPercentages(Long projectId, String period, boolean isWeekly) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<ProductionPlan> productionPlans = productionPlanRepository.findByProjectId(projectId);

        if (productionPlans.isEmpty()) {
            throw new RuntimeException("No production plans found for projectId: " + projectId);
        }

        List<ProductionPlanDetail> details;

        if (PlanningType.FIXED.toString().equalsIgnoreCase(project.getPlanningType())) {
            // If project is fixed, get all production plan details without filtering by month/week
            details = productionPlanDetailRepository.findAllByProductionPlanIdIn(
                    productionPlans.stream().map(ProductionPlan::getId).collect(Collectors.toList())
            );
        } else {
            String currentPeriod = (period == null || period.isBlank()) ? getCurrentMonth() : period;

            List<Long> planIds = productionPlans.stream()
                    .filter(plan -> isWeekly
                            ? (plan.getWeek() != null && plan.getWeek().equals(currentPeriod))
                            : (plan.getMonth() != null && plan.getMonth().equals(currentPeriod)))
                    .map(ProductionPlan::getId)
                    .collect(Collectors.toList());

            if (planIds.isEmpty()) {
                throw new RuntimeException("No production plans found for the given period: " + currentPeriod);
            }

            details = productionPlanDetailRepository.findAllByProductionPlanIdIn(planIds);
        }

        return details.stream()
                .map(detail -> new ModelPercentageDTO(
                        detail.getModel().getId(),
                        detail.getProductionPlan().getMonth(),
                        detail.getProductionPlan().getWeek(),
                        detail.getPercentage()))
                .sorted(Comparator.comparing(ModelPercentageDTO::getPercentage).reversed())
                .collect(Collectors.toList());
    }


    private String getCurrentMonth() {
        return YearMonth.now().toString(); // "YYYY-MM"
    }

    private String getCurrentWeek() {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekNumber = today.get(weekFields.weekOfWeekBasedYear());
        return Year.now().getValue() + "-" + String.format("%02d", weekNumber); // "YYYY-WW"
    }

}
