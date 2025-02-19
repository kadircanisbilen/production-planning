package com.production.planning.service.impl;

import com.production.planning.dto.PartProductionDTO;
import com.production.planning.entity.Model;
import com.production.planning.entity.ModelPart;
import com.production.planning.entity.Part;
import com.production.planning.entity.ProductionPlan;
import com.production.planning.entity.ProductionPlanDetail;
import com.production.planning.entity.Project;
import com.production.planning.repository.ProductionPlanDetailRepository;
import com.production.planning.repository.ProductionPlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductionCalculationServiceImplTest {

    @Mock
    private ProductionPlanRepository productionPlanRepository;

    @Mock
    private ProductionPlanDetailRepository productionPlanDetailRepository;

    @InjectMocks
    private ProductionCalculationServiceImpl productionCalculationService;

    private ProductionPlan testProductionPlan;
    private ProductionPlanDetail testPlanDetail;
    private Model testModel;
    private ModelPart testModelPart;
    private Part testPart;

    @BeforeEach
    void setUp() {
        // Create a sample project
        Project testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");

        // Create a sample production plan
        testProductionPlan = new ProductionPlan();
        testProductionPlan.setId(1L);
        testProductionPlan.setProject(testProject);
        testProductionPlan.setTotalProduction(1000);
        testProductionPlan.setMonth("2025-03");

        // Create a sample model
        testModel = new Model();
        testModel.setId(1L);
        testModel.setName("Model X");
        testModel.setActive(true);
        testModel.setProject(testProject);

        // Create a part
        testPart = new Part();
        testPart.setId(1L);
        testPart.setName("Engine");

        // Create model-part relationship
        testModelPart = new ModelPart();
        testModelPart.setModel(testModel);
        testModelPart.setPart(testPart);
        testModelPart.setQuantity(2);
        testModel.setModelParts(Collections.singletonList(testModelPart));

        // Create a production plan detail
        testPlanDetail = new ProductionPlanDetail();
        testPlanDetail.setId(1L);
        testPlanDetail.setModel(testModel);
        testPlanDetail.setProductionPlan(testProductionPlan);
        testPlanDetail.setPercentage(0.5); // 50%
    }

    @Test
    void shouldCalculatePartProductionSuccessfully() {
        when(productionPlanRepository.findById(1L)).thenReturn(Optional.of(testProductionPlan));
        when(productionPlanDetailRepository.findByProductionPlan(testProductionPlan)).thenReturn(List.of(testPlanDetail));

        List<PartProductionDTO> results = productionCalculationService.calculatePartProduction(1L);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getPartId());
        assertEquals((int) (1000 * 0.5 * 2), results.get(0).getRequiredProduction());
    }

    @Test
    void shouldThrowExceptionWhenProductionPlanNotFound() {
        when(productionPlanRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> productionCalculationService.calculatePartProduction(99L));

        assertEquals("Production Plan with ID 99 not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNoProductionPlanDetailsFound() {
        when(productionPlanRepository.findById(1L)).thenReturn(Optional.of(testProductionPlan));
        when(productionPlanDetailRepository.findByProductionPlan(testProductionPlan)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(RuntimeException.class,
                () -> productionCalculationService.calculatePartProduction(1L));

        assertEquals("No production details available for this production plan.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTotalProductionIsNull() {
        testProductionPlan.setTotalProduction(null);

        when(productionPlanRepository.findById(1L)).thenReturn(Optional.of(testProductionPlan));
        when(productionPlanDetailRepository.findByProductionPlan(testProductionPlan)).thenReturn(List.of(testPlanDetail));

        Exception exception = assertThrows(RuntimeException.class,
                () -> productionCalculationService.calculatePartProduction(1L));

        assertEquals("Total production is missing for production plan ID: 1", exception.getMessage());
    }

    @Test
    void shouldSkipInactiveModels() {
        testModel.setActive(false);

        when(productionPlanRepository.findById(1L)).thenReturn(Optional.of(testProductionPlan));
        when(productionPlanDetailRepository.findByProductionPlan(testProductionPlan)).thenReturn(List.of(testPlanDetail));

        List<PartProductionDTO> results = productionCalculationService.calculatePartProduction(1L);

        assertTrue(results.isEmpty());
    }
}
