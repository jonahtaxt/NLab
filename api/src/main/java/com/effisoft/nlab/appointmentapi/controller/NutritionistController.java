package com.effisoft.nlab.appointmentapi.controller;

import com.effisoft.nlab.appointmentapi.entity.Nutritionist;
import com.effisoft.nlab.appointmentapi.service.NutritionistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nutritionists")
public class NutritionistController {
    private final NutritionistService nutritionistService;

    public NutritionistController(NutritionistService nutritionistService) {
        this.nutritionistService = nutritionistService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")  // Match the casing from Keycloak
    public ResponseEntity<Nutritionist> createNutritionist(@RequestBody Nutritionist nutritionist) {
        Nutritionist createdNutritionist = nutritionistService.createNutritionist(nutritionist);
        return new ResponseEntity<>(createdNutritionist, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")  // Match the casing from Keycloak
    public ResponseEntity<List<Nutritionist>> getAllActiveNutritionists() {
        List<Nutritionist> activeNutritionists = nutritionistService.getAllActiveNutritionists();
        return ResponseEntity.ok(activeNutritionists);
    }
}