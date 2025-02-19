package com.production.planning.mapper;

import com.production.planning.dto.ModelDTO;
import com.production.planning.dto.ModelRequestDTO;
import com.production.planning.entity.Model;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ModelMapper {
    ModelDTO toModelDTO(Model model);
    Model toModel(ModelRequestDTO dto);
}
