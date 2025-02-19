package com.production.planning.service.impl;

import com.production.planning.entity.Part;
import com.production.planning.repository.PartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PartServiceImplTest {

    @Mock
    private PartRepository partRepository;

    @InjectMocks
    private PartServiceImpl partService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllParts() {
        // Given
        Part part1 = new Part();
        part1.setId(1L);
        part1.setName("Part 1");
        part1.setActive(true);

        Part part2 = new Part();
        part2.setId(2L);
        part2.setName("Part 2");
        part2.setActive(true);

        List<Part> parts = Arrays.asList(part1, part2);
        when(partRepository.findAllActiveParts()).thenReturn(parts);

        // When
        List<Part> result = partService.getAllParts();

        // Then
        assertEquals(2, result.size());
        verify(partRepository, times(1)).findAllActiveParts();
    }

    @Test
    void testGetPartById() {
        // Given
        Part part = new Part();
        part.setId(1L);
        part.setName("Part 1");
        part.setActive(true);

        when(partRepository.findById(1L)).thenReturn(Optional.of(part));

        // When
        Part result = partService.getPartById(1L);

        // Then
        assertNotNull(result);
        assertEquals("Part 1", result.getName());
        verify(partRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPartById_NotFound() {
        // Given
        when(partRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RuntimeException.class, () -> partService.getPartById(1L));
        verify(partRepository, times(1)).findById(1L);
    }

    @Test
    void testCreatePart() {
        // Given
        Part part = new Part();
        part.setName("New Part");
        part.setActive(true);

        when(partRepository.save(part)).thenReturn(part);

        // When
        Part result = partService.createPart(part);

        // Then
        assertNotNull(result);
        assertEquals("New Part", result.getName());
        verify(partRepository, times(1)).save(part);
    }

    @Test
    void testUpdatePart() {
        // Given
        Part existingPart = new Part();
        existingPart.setId(1L);
        existingPart.setName("Existing Part");
        existingPart.setActive(true);

        Part updatedPart = new Part();
        updatedPart.setName("Updated Part");

        when(partRepository.findById(1L)).thenReturn(Optional.of(existingPart));
        when(partRepository.save(existingPart)).thenReturn(existingPart);

        // When
        Part result = partService.updatePart(1L, updatedPart);

        // Then
        assertNotNull(result);
        assertEquals("Updated Part", result.getName());
        verify(partRepository, times(1)).findById(1L);
        verify(partRepository, times(1)).save(existingPart);
    }

    @Test
    void testUpdatePart_NotActive() {
        // Given
        Part existingPart = new Part();
        existingPart.setId(1L);
        existingPart.setName("Existing Part");
        existingPart.setActive(false);

        Part updatedPart = new Part();
        updatedPart.setName("Updated Part");

        when(partRepository.findById(1L)).thenReturn(Optional.of(existingPart));

        // When / Then
        assertThrows(RuntimeException.class, () -> partService.updatePart(1L, updatedPart));
        verify(partRepository, times(1)).findById(1L);
        verify(partRepository, never()).save(existingPart);
    }

    @Test
    void testDeletePart() {
        // Given
        Part part = new Part();
        part.setId(1L);
        part.setName("Part 1");
        part.setActive(true);

        when(partRepository.findById(1L)).thenReturn(Optional.of(part));
        when(partRepository.save(part)).thenReturn(part);

        // When
        partService.deletePart(1L);

        // Then
        assertFalse(part.getActive());
        verify(partRepository, times(1)).findById(1L);
        verify(partRepository, times(1)).save(part);
    }

    @Test
    void testDeletePart_NotActive() {
        // Given
        Part part = new Part();
        part.setId(1L);
        part.setName("Part 1");
        part.setActive(false);

        when(partRepository.findById(1L)).thenReturn(Optional.of(part));

        // When / Then
        assertThrows(RuntimeException.class, () -> partService.deletePart(1L));
        verify(partRepository, times(1)).findById(1L);
        verify(partRepository, never()).save(part);
    }
}