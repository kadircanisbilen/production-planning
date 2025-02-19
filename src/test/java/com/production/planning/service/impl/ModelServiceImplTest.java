package com.production.planning.service.impl;

import com.production.planning.dto.ModelPercentageDTO;
import com.production.planning.entity.Model;
import com.production.planning.entity.ProductionPlan;
import com.production.planning.entity.ProductionPlanDetail;
import com.production.planning.entity.Project;
import com.production.planning.repository.ModelPartRepository;
import com.production.planning.repository.ModelRepository;
import com.production.planning.repository.PartRepository;
import com.production.planning.repository.ProductionPlanDetailRepository;
import com.production.planning.repository.ProductionPlanRepository;
import com.production.planning.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModelServiceImplTest {

    @Mock
    private ModelRepository modelRepository;

    @Mock
    private ProductionPlanDetailRepository productionPlanDetailRepository;

    @Mock
    private ProductionPlanRepository productionPlanRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ModelPartRepository modelPartRepository;

    @Mock
    private PartRepository partRepository;

    @InjectMocks
    private ModelServiceImpl modelService;

    private Project testProject;
    private ProductionPlan testPlan;
    private ProductionPlanDetail testDetail;
    private Model testModel;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setPlanningType("monthly");
        testProject.setActive(true);
        testProject.setModels(new ArrayList<>());

        testModel = new Model();
        testModel.setId(1L);
        testModel.setName("Model X");
        testModel.setProject(testProject);
        testModel.setActive(true);

        testPlan = new ProductionPlan();
        testPlan.setId(1L);
        testPlan.setProject(testProject);
        testPlan.setMonth("2025-03");
        testPlan.setTotalProduction(1000);

        testDetail = new ProductionPlanDetail();
        testDetail.setId(1L);
        testDetail.setProductionPlan(testPlan);
        testDetail.setModel(testModel);
        testDetail.setPercentage(0.5);
    }

    @Test
    void shouldReturnAllActiveModels() {
        when(modelRepository.findAllActiveModels()).thenReturn(List.of(testModel));
        List<Model> models = modelService.getAllModels();
        assertFalse(models.isEmpty());
        assertEquals(1, models.size());
    }

    @Test
    void shouldReturnModelById() {
        when(modelRepository.findById(1L)).thenReturn(Optional.of(testModel));
        Model model = modelService.getModelById(1L);
        assertNotNull(model);
        assertEquals("Model X", model.getName());
    }

    @Test
    void shouldThrowExceptionWhenModelNotFound() {
        when(modelRepository.findById(99L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> modelService.getModelById(99L));
        assertEquals("Model not found", exception.getMessage());
    }

    @Test
    void shouldCreateNewModel() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(modelRepository.save(any(Model.class))).thenReturn(testModel);
        Model createdModel = modelService.createModel(testModel, Map.of());
        assertNotNull(createdModel);
        assertEquals("Model X", createdModel.getName());
    }

    @Test
    void shouldUpdateModel() {
        Model updatedModel = new Model();
        updatedModel.setName("Updated Model");
        when(modelRepository.findById(1L)).thenReturn(Optional.of(testModel));
        when(modelRepository.save(any(Model.class))).thenReturn(updatedModel);
        Model result = modelService.updateModel(1L, updatedModel);
        assertEquals("Updated Model", result.getName());
    }

    @Test
    void shouldSoftDeleteModel() {
        when(modelRepository.findById(1L)).thenReturn(Optional.of(testModel));
        modelService.deleteModel(1L);
        assertFalse(testModel.getActive());
        verify(modelRepository, times(1)).save(testModel);
    }

    @Test
    void shouldUpdateModelPercentage() {
        ProductionPlanDetail detail = new ProductionPlanDetail();
        detail.setModel(testModel);
        List<ProductionPlanDetail> details = List.of(detail);
        when(modelRepository.findById(1L)).thenReturn(Optional.of(testModel));
        when(productionPlanDetailRepository.findByModelId(1L)).thenReturn(details);
        modelService.updateModelPercentage(1L, 0.5);
        assertEquals(0.5, details.get(0).getPercentage());
    }

    @Test
    void shouldUpdateModelStatus() {

        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setPlanningType("fixed");
        testProject.setActive(true);
        testProject.setModels(new ArrayList<>());

        testModel = new Model();
        testModel.setId(1L);
        testModel.setName("Model X");
        testModel.setProject(testProject);
        testModel.setActive(true);

        when(modelRepository.findById(1L)).thenReturn(Optional.of(testModel));
        modelService.updateModelStatus(1L, false);
        assertFalse(testModel.getActive());
    }

    @Test
    void shouldThrowExceptionWhenProjectNotFoundForDateRange() {
        when(projectRepository.findById(testProject.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                modelService.getModelPercentagesByDateRange(testProject.getId(), "2025-03", "2025-05"));

        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenProjectIsNotMonthly() {
        testProject.setPlanningType("fixed");
        when(projectRepository.findById(testProject.getId())).thenReturn(Optional.of(testProject));

        Exception exception = assertThrows(RuntimeException.class, () ->
                modelService.getModelPercentagesByDateRange(testProject.getId(), "2025-03", "2025-05"));

        assertEquals("Model percentages can only be retrieved for 'monthly' planning projects.", exception.getMessage());
    }

    @Test
    void shouldReturnModelPercentagesForDateRange() {
        when(projectRepository.findById(testProject.getId())).thenReturn(Optional.of(testProject));
        when(productionPlanDetailRepository.findByProjectIdAndDateRange(anyLong(), anyString(), anyString()))
                .thenReturn(List.of(testDetail));

        List<ModelPercentageDTO> results = modelService.getModelPercentagesByDateRange(testProject.getId(), "2025-03", "2025-05");

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals("2025-03", results.get(0).getMonth());
    }

    @Test
    void shouldThrowExceptionWhenProjectNotFoundForSortedModelPercentages() {
        when(projectRepository.findById(testProject.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                modelService.getSortedModelPercentages(testProject.getId(), "2025-03", false));

        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNoProductionPlansExist() {
        when(projectRepository.findById(testProject.getId())).thenReturn(Optional.of(testProject));
        when(productionPlanRepository.findByProjectId(testProject.getId())).thenReturn(List.of());

        Exception exception = assertThrows(RuntimeException.class, () ->
                modelService.getSortedModelPercentages(testProject.getId(), "2025-03", false));

        assertEquals("No production plans found for projectId: 1", exception.getMessage());
    }

    @Test
    void shouldReturnSortedModelPercentagesForFixedPlanning() {
        testProject.setPlanningType("fixed");
        when(projectRepository.findById(testProject.getId())).thenReturn(Optional.of(testProject));
        when(productionPlanRepository.findByProjectId(testProject.getId())).thenReturn(List.of(testPlan));
        when(productionPlanDetailRepository.findAllByProductionPlanIdIn(anyList())).thenReturn(List.of(testDetail));

        List<ModelPercentageDTO> results = modelService.getSortedModelPercentages(testProject.getId(), "2025-03", false);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    void shouldReturnSortedModelPercentagesForMonthlyOrWeeklyPlanning() {
        testProject.setPlanningType("monthly");
        when(projectRepository.findById(testProject.getId())).thenReturn(Optional.of(testProject));
        when(productionPlanRepository.findByProjectId(testProject.getId())).thenReturn(List.of(testPlan));
        when(productionPlanDetailRepository.findAllByProductionPlanIdIn(anyList())).thenReturn(List.of(testDetail));

        List<ModelPercentageDTO> results = modelService.getSortedModelPercentages(testProject.getId(), "2025-03", false);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    void shouldReturnSortedModelPercentagesDescendingOrder() {
        ProductionPlanDetail detail1 = new ProductionPlanDetail();
        detail1.setId(2L);
        detail1.setProductionPlan(testPlan);
        detail1.setModel(testModel);
        detail1.setPercentage(0.3);

        when(projectRepository.findById(testProject.getId())).thenReturn(Optional.of(testProject));
        when(productionPlanRepository.findByProjectId(testProject.getId())).thenReturn(List.of(testPlan));
        when(productionPlanDetailRepository.findAllByProductionPlanIdIn(anyList())).thenReturn(List.of(testDetail, detail1));

        List<ModelPercentageDTO> results = modelService.getSortedModelPercentages(testProject.getId(), "2025-03", false);

        assertFalse(results.isEmpty());
        assertEquals(2, results.size());
        assertTrue(results.get(0).getPercentage() > results.get(1).getPercentage()); // Ensure descending order
    }
}
