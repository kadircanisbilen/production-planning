package com.production.planning.controller;

import com.production.planning.dto.PartDTO;
import com.production.planning.dto.PartRequestDTO;
import com.production.planning.entity.Part;
import com.production.planning.mapper.PartMapper;
import com.production.planning.service.PartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parts")
@RequiredArgsConstructor
public class PartController {

    private final PartService partService;
    private final PartMapper partMapper;

    @GetMapping
    public ResponseEntity<List<PartDTO>> getAllParts() {
        List<PartDTO> dtos = partService.getAllParts().stream()
                .map(partMapper::toPartDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartDTO> getPartById(@PathVariable Long id) {
        Part part = partService.getPartById(id);
        return ResponseEntity.ok(partMapper.toPartDTO(part));
    }

    @PostMapping
    public ResponseEntity<PartDTO> createPart(@RequestBody PartRequestDTO requestDTO) {
        Part part = partMapper.toPart(requestDTO);
        Part created = partService.createPart(part);
        return ResponseEntity.ok(partMapper.toPartDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartDTO> updatePart(@PathVariable Long id, @RequestBody PartRequestDTO requestDTO) {
        Part part = partMapper.toPart(requestDTO);
        Part updated = partService.updatePart(id, part);
        return ResponseEntity.ok(partMapper.toPartDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable Long id) {
        partService.deletePart(id);
        return ResponseEntity.noContent().build();
    }
}
