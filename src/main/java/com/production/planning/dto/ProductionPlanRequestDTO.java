package com.production.planning.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionPlanRequestDTO {

    @NotNull
    private Long projectId;

    private String month;

    private String week;

    @NotNull
    @Min(value = 1, message = "Total production must be at least 1")
    private Integer totalProduction;
}
