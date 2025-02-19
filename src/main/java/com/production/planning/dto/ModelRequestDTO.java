package com.production.planning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelRequestDTO {
    private String name;
    private Long projectId;
    private Map<Long, Integer> parts; // partId -> quantity
}
