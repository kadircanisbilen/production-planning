package com.production.planning.mapper;

import com.production.planning.dto.ProjectDTO;
import com.production.planning.dto.ProjectRequestDTO;
import com.production.planning.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectDTO toProjectDTO(Project project);

    Project toProject(ProjectRequestDTO dto);
}
