package com.production.planning.controller;

import com.production.planning.dto.ProjectDTO;
import com.production.planning.dto.ProjectRequestDTO;
import com.production.planning.dto.ProjectUpdateDTO;
import com.production.planning.entity.Project;
import com.production.planning.mapper.ProjectMapper;
import com.production.planning.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<ProjectDTO> dtos = projectService.getAllProjects().stream()
                .map(projectMapper::toProjectDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectRequestDTO requestDTO) {
        Project project = projectMapper.toProject(requestDTO);
        Project created = projectService.createProject(project);
        return ResponseEntity.ok(projectMapper.toProjectDTO(created));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/settings")
    public ResponseEntity<ProjectDTO> updateProjectSettings(
            @PathVariable Long id,
            @RequestBody ProjectUpdateDTO updateDTO) {
        Project updatedProject = projectService.updateProjectSettings(id, updateDTO);
        return ResponseEntity.ok(new ProjectDTO(updatedProject.getId(), updatedProject.getName(), updatedProject.getPlanningType()));
    }
}
