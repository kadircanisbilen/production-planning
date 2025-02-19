package com.production.planning.service.impl;

import com.production.planning.annotation.LogOperation;
import com.production.planning.entity.Part;
import com.production.planning.repository.PartRepository;
import com.production.planning.service.PartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartServiceImpl implements PartService {

    private final PartRepository partRepository;

    @Override
    public List<Part> getAllParts() {
        return partRepository.findAllActiveParts();
    }

    @Override
    public Part getPartById(Long id) {
        return partRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Part not found"));
    }

    @Override
    @Transactional
    @LogOperation(operationType = "CREATE")
    public Part createPart(Part part) {
        return partRepository.save(part);
    }

    @Override
    @Transactional
    @LogOperation(operationType = "UPDATE")
    public Part updatePart(Long id, Part part) {
        Part existing = getPartById(id);
        if (!existing.getActive()) {
            throw new RuntimeException("This part has been deleted and cannot be modified.");
        }
        existing.setName(part.getName());
        return partRepository.save(existing);
    }

    @Override
    @Transactional
    @LogOperation(operationType = "SOFT_DELETE")
    public void deletePart(Long id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Part not found"));

        if (!part.getActive()) {
            throw new RuntimeException("This part has been deleted and cannot be modified.");
        }

        part.deactivate();
        partRepository.save(part);
    }
}

