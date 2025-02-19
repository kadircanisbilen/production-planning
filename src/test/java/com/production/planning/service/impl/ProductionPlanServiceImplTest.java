package com.production.planning.service.impl;

import com.production.planning.entity.ProductionPlan;
import com.production.planning.entity.Project;
import com.production.planning.repository.ProductionPlanRepository;
import com.production.planning.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductionPlanServiceImplTest {

    @Mock
    private ProductionPlanRepository productionPlanRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProductionPlanServiceImpl productionPlanService;

    private Project testProject;
    private ProductionPlan testPlan;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setPlanningType("fixed");
        testProject.setActive(true);

        testPlan = new ProductionPlan();
        testPlan.setId(1L);
        testPlan.setProject(testProject);
        testPlan.setMonth("2025-03");
        testPlan.setTotalProduction(1000);
    }

    @Test
    void shouldReturnAllProductionPlans() {
        when(productionPlanRepository.findAll()).thenReturn(List.of(testPlan));
        List<ProductionPlan> plans = productionPlanService.getAllProductionPlans();
        assertFalse(plans.isEmpty());
        assertEquals(1, plans.size());
    }

    @Test
    void shouldCreateProductionPlanWithValidMonth() {
        when(projectRepository.findById(testProject.getId())).thenReturn(Optional.of(testProject));
        when(productionPlanRepository.save(any(ProductionPlan.class))).thenReturn(testPlan);

        ProductionPlan createdPlan = productionPlanService.createProductionPlan(testPlan);

        assertNotNull(createdPlan);
        assertEquals("2025-03", createdPlan.getMonth());
        assertNull(createdPlan.getWeek());
        verify(productionPlanRepository, times(1)).save(testPlan);
    }

    @Test
    void shouldCreateProductionPlanWithValidWeek() {
        testPlan.setWeek("2025-W10");
        testPlan.setMonth(null);

        when(projectRepository.findById(testProject.getId())).thenReturn(Optional.of(testProject));
        when(productionPlanRepository.save(any(ProductionPlan.class))).thenReturn(testPlan);

        ProductionPlan createdPlan = productionPlanService.createProductionPlan(testPlan);

        assertNotNull(createdPlan);
        assertEquals("2025-W10", createdPlan.getWeek());
        assertNull(createdPlan.getMonth());
        verify(productionPlanRepository, times(1)).save(testPlan);
    }

    @Test
    void shouldThrowExceptionWhenProjectNotFound() {
        when(projectRepository.findById(testProject.getId())).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> productionPlanService.createProductionPlan(testPlan));
        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNeitherMonthNorWeekProvided() {
        testPlan.setMonth(null);
        testPlan.setWeek(null);

        when(projectRepository.findById(testProject.getId())).thenReturn(Optional.of(testProject));

        Exception exception = assertThrows(RuntimeException.class, () -> productionPlanService.createProductionPlan(testPlan));
        assertEquals("Either month or week must be provided for the production plan.", exception.getMessage());
    }
}
