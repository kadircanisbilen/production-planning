package com.production.planning.mapper;

import com.production.planning.dto.ProductionPlanDTO;
import com.production.planning.dto.ProductionPlanRequestDTO;
import com.production.planning.entity.ProductionPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.YearMonth;

@Mapper(componentModel = "spring")
public interface ProductionPlanMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "month", target = "month")
    ProductionPlanDTO toProductionPlanDTO(ProductionPlan plan);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "month", target = "month")
    ProductionPlan toProductionPlan(ProductionPlanRequestDTO dto);
}
