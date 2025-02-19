package com.production.planning.service;

import com.production.planning.entity.Part;
import java.util.List;

public interface PartService {
    List<Part> getAllParts();
    Part getPartById(Long id);
    Part createPart(Part part);
    Part updatePart(Long id, Part part);
    void deletePart(Long id);
}
