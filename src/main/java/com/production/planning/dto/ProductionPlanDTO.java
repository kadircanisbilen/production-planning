package com.production.planning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductionPlanDTO {
    private Long id;
    private Long projectId;
    private String month; // "YYYY-MM"
    private String week; // "YYYY-WW"
    private Integer totalProduction;
}
