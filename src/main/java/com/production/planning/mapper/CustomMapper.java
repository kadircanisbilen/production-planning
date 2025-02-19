package com.production.planning.mapper;

import com.production.planning.dto.ModelDTO;
import com.production.planning.dto.ModelRequestDTO;
import com.production.planning.entity.Model;
import com.production.planning.entity.ModelPart;
import com.production.planning.entity.Part;
import com.production.planning.entity.Project;
import com.production.planning.repository.PartRepository;
import com.production.planning.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomMapper {

    private final ProjectRepository projectRepository;
    private final PartRepository partRepository;

    public Model requestToModel(ModelRequestDTO dto) {
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + dto.getProjectId()));

        List<ModelPart> modelParts = dto.getParts().entrySet().stream()
                .map(entry -> {
                    Part part = partRepository.findById(entry.getKey())
                            .orElseThrow(() -> new RuntimeException("Part not found with ID: " + entry.getKey()));

                    return ModelPart.builder()
                            .part(part)
                            .quantity(entry.getValue())
                            .build();
                })
                .collect(Collectors.toList());

        // Build Model entity
        Model model = Model.builder()
                .project(project)
                .name(dto.getName())
                .modelParts(modelParts)
                .build();

        modelParts.forEach(mp -> mp.setModel(model));

        return model;
    }

    public ModelDTO toModelDTO(Model model) {
        if (model == null) {
            return null;
        }

        return ModelDTO.builder()
                .id(model.getId())
                .name(model.getName())
                .projectId(model.getProject().getId())
                .parts(mapModelPartsToMap(model.getModelParts()))
                .build();
    }

    public List<ModelDTO> toModelDTOList(List<Model> models) {
        return models.stream()
                .map(this::toModelDTO)
                .collect(Collectors.toList());
    }

    private Map<String, Integer> mapModelPartsToMap(List<ModelPart> modelParts) {
        if (modelParts == null) {
            return Collections.emptyMap();
        }
        return modelParts.stream()
                .collect(Collectors.toMap(mp -> mp.getPart().getName(), ModelPart::getQuantity));
    }
}
