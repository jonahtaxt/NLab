package com.effisoft.nlab.appointmentapi.controller;

import com.effisoft.nlab.appointmentapi.dto.NutritionistDTO;
import com.effisoft.nlab.appointmentapi.entity.Nutritionist;
import com.effisoft.nlab.appointmentapi.service.NutritionistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<List<Nutritionist>> getAllActiveNutritionists() {
        List<Nutritionist> activeNutritionists = nutritionistService.getAllActiveNutritionists();
        return ResponseEntity.ok(activeNutritionists);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateNutritionist(@PathVariable Integer id) {
        nutritionistService.deactivateNutritionist(id);
        return ResponseEntity.noContent().build();
    }
}