package com.effisoft.nlab.appointmentapi.controller;

import com.effisoft.nlab.appointmentapi.entity.Nutritionist;
import com.effisoft.nlab.appointmentapi.service.NutritionistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/nutritionists")
public class NutritionistController {
    private final NutritionistService nutritionistService;

    public NutritionistController(NutritionistService nutritionistService) {
        this.nutritionistService = nutritionistService;
    }

    @PostMapping
    public ResponseEntity<Nutritionist> createNutritionist(@RequestBody Nutritionist nutritionist) {
        Nutritionist createdNutritionist = nutritionistService.createNutritionist(nutritionist);
        return new ResponseEntity<>(createdNutritionist, HttpStatus.CREATED);
    }
}
