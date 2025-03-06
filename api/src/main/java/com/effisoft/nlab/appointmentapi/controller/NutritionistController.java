package com.effisoft.nlab.appointmentapi.controller;

import com.effisoft.nlab.appointmentapi.dto.NutritionistDTO;
import com.effisoft.nlab.appointmentapi.dto.PageResponseDTO;
import com.effisoft.nlab.appointmentapi.entity.Nutritionist;
import com.effisoft.nlab.appointmentapi.service.NutritionistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nutritionists")
@Validated
@RequiredArgsConstructor
public class NutritionistController {
    private final NutritionistService nutritionistService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Nutritionist> createNutritionist(
            @Valid @RequestBody NutritionistDTO nutritionistDTO) {
        Nutritionist createdNutritionist = nutritionistService.createNutritionist(nutritionistDTO);
        return new ResponseEntity<>(createdNutritionist, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Nutritionist> updateNutritionist(
            @PathVariable Integer id,
            @Valid @RequestBody NutritionistDTO nutritionistDTO) {
        Nutritionist updatedNutritionist = nutritionistService.updateNutritionist(id, nutritionistDTO);
        return ResponseEntity.ok(updatedNutritionist);
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<PageResponseDTO<NutritionistDTO>> getAllNutritionists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Boolean active) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<NutritionistDTO> nutritionists = nutritionistService.getNutritionists(pageable, searchTerm, active);

        PageResponseDTO<NutritionistDTO> response = new PageResponseDTO<>(
                nutritionists.getContent(),
                nutritionists.getNumber(),
                nutritionists.getSize(),
                nutritionists.getTotalElements(),
                nutritionists.getTotalPages(),
                nutritionists.isFirst(),
                nutritionists.isLast());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateNutritionist(@PathVariable Integer id) {
        nutritionistService.deactivateNutritionist(id);
        return ResponseEntity.noContent().build();
    }
}