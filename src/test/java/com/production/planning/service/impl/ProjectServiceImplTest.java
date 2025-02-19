package com.production.planning.service.impl;

import com.production.planning.dto.ModelPercentageDTO;
import com.production.planning.dto.ProjectUpdateDTO;
import com.production.planning.entity.Model;
import com.production.planning.entity.ProductionPlan;
import com.production.planning.entity.ProductionPlanDetail;
import com.production.planning.entity.Project;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProductionPlanRepository productionPlanRepository;

    @Mock
    private ProductionPlanDetailRepository productionPlanDetailRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project testProject;
    private ProductionPlan testPlan;
    private ProductionPlanDetail testDetail;
    private ProjectUpdateDTO updateDTO;
    private Model testModel;

    @BeforeEach
    void setUp() {
        testModel = Model.builder()
                .id(1L)
                .name("Test Model")
                .build();

        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setPlanningType("monthly");
        testProject.setActive(true);
        testProject.setModels(Collections.singletonList(testModel));

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

        updateDTO = new ProjectUpdateDTO();
        updateDTO.setPlanningType("weekly");
        updateDTO.setModelPercentages(List.of(
                new ModelPercentageDTO(1L, "2025-03", null, 0.7)
        ));

    }


    @Test
    void shouldReturnAllProjects() {
        when(projectRepository.findAll()).thenReturn(List.of(testProject));
        List<Project> projects = projectService.getAllProjects();
        assertFalse(projects.isEmpty());
        assertEquals(1, projects.size());
    }

    @Test
    void shouldCreateProject() {
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        Project createdProject = projectService.createProject(testProject);
        assertNotNull(createdProject);
        assertEquals("Test Project", createdProject.getName());
    }

    @Test
    void shouldSoftDeleteProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        projectService.deleteProject(1L);
        assertFalse(testProject.getActive());
        verify(projectRepository, times(1)).save(testProject);
    }

    @Test
    void shouldUpdateProjectSettings() {
        ProjectUpdateDTO updateDTO = new ProjectUpdateDTO();
        updateDTO.setPlanningType("monthly");
        updateDTO.setModelPercentages(new ArrayList<>());

        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        when(productionPlanRepository.findByProjectId(1L)).thenReturn(Collections.singletonList(testPlan));

        Project updatedProject = projectService.updateProjectSettings(1L, updateDTO);
        assertEquals("monthly", updatedProject.getPlanningType());
    }

    @Test
    void shouldUpdateProjectSettingsAndStorePreviousPercentages() {
        // Mock project
        when(projectRepository.findById(testProject.getId())).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // Mock production plans
        when(productionPlanRepository.findByProjectId(testProject.getId())).thenReturn(List.of(testPlan));

        // Mock production plan details
        when(productionPlanDetailRepository.findAllByProductionPlanIdIn(anyList()))
                .thenReturn(List.of(testDetail));

        // Act - Call the service method
        Project updatedProject = projectService.updateProjectSettings(testProject.getId(), updateDTO);

        // Assert - Ensure the project is updated
        assertNotNull(updatedProject, "Updated project should not be null");
        assertEquals("weekly", updatedProject.getPlanningType());

        // Verify repository interactions
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(productionPlanDetailRepository, times(1)).saveAll(anyList());
        verify(productionPlanDetailRepository, atLeastOnce()).updatePercentageByModelIdAndMonth(anyLong(), anyString(), anyDouble());
    }

    @Test
    void shouldThrowExceptionWhenNoProductionPlansFound() {
        // given
        Project mockProject = new Project();
        mockProject.setId(1L);
        mockProject.setName("Test Project");
        mockProject.setPlanningType("monthly");
        mockProject.setModels(new ArrayList<>());

        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));

        when(productionPlanRepository.findByProjectId(1L)).thenReturn(Collections.emptyList());

        // when & then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            projectService.updateProjectSettings(1L, updateDTO);
        });

        assertEquals("No production plans found for projectId: 1", exception.getMessage());
    }
}
