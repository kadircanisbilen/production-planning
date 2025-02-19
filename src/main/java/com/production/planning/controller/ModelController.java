package com.production.planning.controller;

import com.production.planning.dto.ModelDTO;
import com.production.planning.dto.ModelPercentageDTO;
import com.production.planning.dto.ModelPercentageUpdateDTO;
import com.production.planning.dto.ModelRequestDTO;
import com.production.planning.entity.Model;
import com.production.planning.mapper.CustomMapper;
import com.production.planning.mapper.ModelMapper;
import com.production.planning.service.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
public class ModelController {

    private final ModelService modelService;
    private final ModelMapper modelMapper;
    private final CustomMapper customMapper;

    @GetMapping
    public ResponseEntity<List<ModelDTO>> getAllModels() {
        List<ModelDTO> dtos = customMapper.toModelDTOList(modelService.getAllModels());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModelDTO> getModelById(@PathVariable Long id) {
        Model model = modelService.getModelById(id);
        return ResponseEntity.ok(customMapper.toModelDTO(model));
    }

    @PostMapping
    public ResponseEntity<ModelDTO> createModel(@RequestBody ModelRequestDTO requestDTO) {
        Model model = customMapper.requestToModel(requestDTO);
        Model createdModel = modelService.createModel(model, requestDTO.getParts());
        return ResponseEntity.ok(modelMapper.toModelDTO(createdModel));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModelDTO> updateModel(@PathVariable Long id, @RequestBody ModelRequestDTO requestDTO) {
        Model model = modelMapper.toModel(requestDTO);
        Model updated = modelService.updateModel(id, model);
        return ResponseEntity.ok(modelMapper.toModelDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModel(@PathVariable Long id) {
        modelService.deleteModel(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/percentage")
    public ResponseEntity<Void> updateModelPercentage(@PathVariable Long id, @RequestBody ModelPercentageUpdateDTO dto) {
        modelService.updateModelPercentage(id, dto.getPercentage());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateModelStatus(@PathVariable Long id, @RequestParam boolean active) {
        modelService.updateModelStatus(id, active);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/percentages/{projectId}")
    public ResponseEntity<List<ModelPercentageDTO>> getModelPercentages(
            @PathVariable Long projectId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        List<ModelPercentageDTO> percentages = modelService.getModelPercentagesByDateRange(projectId, startDate, endDate);
        return ResponseEntity.ok(percentages);
    }

    @GetMapping("/{projectId}/sorted-models")
    public ResponseEntity<List<ModelPercentageDTO>> getSortedModels(
            @PathVariable Long projectId,
            @RequestParam(required = false) String period,
            @RequestParam(required = false, defaultValue = "false") boolean isWeekly) {

        List<ModelPercentageDTO> sortedModels = modelService.getSortedModelPercentages(projectId, period, isWeekly);
        return ResponseEntity.ok(sortedModels);
    }

}
