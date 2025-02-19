package com.production.planning.mapper;

import com.production.planning.dto.PartDTO;
import com.production.planning.dto.PartRequestDTO;
import com.production.planning.entity.Part;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PartMapper {
    PartDTO toPartDTO(Part part);
    Part toPart(PartRequestDTO dto);
}
