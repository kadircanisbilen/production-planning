package com.production.planning.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String planningType;
}
