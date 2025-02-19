package com.production.planning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartProductionDTO {
    private Long modelId;
    private String modelName;
    private Long partId;
    private String partName;
    private Integer requiredProduction;
}
