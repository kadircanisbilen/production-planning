package com.production.planning.service;

import com.production.planning.dto.ModelPercentageDTO;
import com.production.planning.entity.Model;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface ModelService {
    List<Model> getAllModels();
    Model getModelById(Long id);
    Model createModel(Model model, Map<Long, Integer> parts);
    Model updateModel(Long id, Model model);
    void deleteModel(Long id);
    void updateModelPercentage(Long modelId, double percentage);
    void updateModelStatus(Long modelId, boolean active);
    List<ModelPercentageDTO> getModelPercentagesByDateRange(Long projectId, String startDate, String endDate);
    List<ModelPercentageDTO> getSortedModelPercentages(Long projectId, String period, boolean isWeekly);
}
