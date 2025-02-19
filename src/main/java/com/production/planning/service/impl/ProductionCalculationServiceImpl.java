package com.production.planning.service.impl;

import com.production.planning.annotation.LogOperation;
import com.production.planning.dto.PartProductionDTO;
import com.production.planning.entity.Model;
import com.production.planning.entity.ProductionPlan;
import com.production.planning.entity.ProductionPlanDetail;
import com.production.planning.repository.ProductionPlanDetailRepository;
import com.production.planning.repository.ProductionPlanRepository;
import com.production.planning.service.ProductionCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductionCalculationServiceImpl implements ProductionCalculationService {

    private final ProductionPlanRepository productionPlanRepository;
    private final ProductionPlanDetailRepository productionPlanDetailRepository;

    /**
     * Calculation logic:
     * 1. Retrieve the total production from the production plan.
     * 2. For each model, calculate the production count: totalProduction * percentage.
     * 3. For each model's parts, compute the required production:
     *    requiredProduction = modelProductionCount * modelPart.quantity.
     */
    @Override
    @Transactional
    @LogOperation(operationType = "CALCULATE")
    public List<PartProductionDTO> calculatePartProduction(Long productionPlanId) {
        log.info("Calculating part production for productionPlanId: {}", productionPlanId);

        // Retrieve the production plan
        ProductionPlan plan = productionPlanRepository.findById(productionPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Production Plan with ID " + productionPlanId + " not found"));

        // Retrieve all production plan details
        List<ProductionPlanDetail> details = productionPlanDetailRepository.findByProductionPlan(plan);

        if (details.isEmpty()) {
            log.warn("No production plan details found for productionPlanId: {}", productionPlanId);
            throw new RuntimeException("No production details available for this production plan.");
        }

        // Ensure totalProduction is not null
        if (plan.getTotalProduction() == null) {
            throw new RuntimeException("Total production is missing for production plan ID: " + productionPlanId);
        }

        List<PartProductionDTO> result = details.stream()
                .filter(detail -> {
                    if (detail.getPercentage() == null) {
                        log.warn("Skipping model with ID {} due to missing percentage", detail.getModel().getId());
                        return false;
                    }
                    return true;
                })
                .flatMap(detail -> {
                    Model model = detail.getModel();

                    // Skip inactive models
                    if (!model.getActive()) {
                        log.info("Skipping inactive model with ID: {}", model.getId());
                        return Stream.empty();
                    }

                    int modelProductionCount = (int) Math.round(plan.getTotalProduction() * detail.getPercentage());

                    return model.getModelParts().stream()
                            .map(mp -> new PartProductionDTO(
                                    model.getId(),
                                    model.getName(),
                                    mp.getPart().getId(),
                                    mp.getPart().getName(),
                                    modelProductionCount * mp.getQuantity()
                            ));
                })
                .collect(Collectors.toList());

        log.info("Part production calculation completed for productionPlanId: {}, total parts calculated: {}",
                productionPlanId, result.size());

        return result;
    }

}
