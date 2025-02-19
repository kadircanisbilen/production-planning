package com.production.planning.service;

import com.production.planning.dto.ProjectUpdateDTO;
import com.production.planning.entity.Project;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProjectService {
    List<Project> getAllProjects();
    Project createProject(Project project);
    void deleteProject(Long id);
    Project updateProjectSettings(Long projectId, ProjectUpdateDTO updateDTO);
}
