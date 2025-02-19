package com.production.planning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelPercentageDTO {
    private Long modelId;
    private String month;
    private String week;
    private double percentage;
}
