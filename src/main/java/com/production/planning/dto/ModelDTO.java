package com.production.planning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModelDTO {
    private Long id;
    private String name;
    private Long projectId;
    private Map<String, Integer> parts;
}
