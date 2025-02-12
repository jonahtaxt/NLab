package com.effisoft.nlab.appointmentapi.controller;

import com.effisoft.nlab.appointmentapi.dto.NutritionistPaymentPeriodDTO;
import com.effisoft.nlab.appointmentapi.entity.NutritionistPaymentPeriod;
import com.effisoft.nlab.appointmentapi.service.NutritionistPaymentPeriodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nutritionist-payment-periods")
@RequiredArgsConstructor
public class NutritionistPaymentPeriodController {
    private final NutritionistPaymentPeriodService nutritionistPaymentPeriodService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NutritionistPaymentPeriod> createPaymentPeriod(
            @Valid @RequestBody NutritionistPaymentPeriodDTO paymentPeriodDTO) {
        NutritionistPaymentPeriod createdPeriod = nutritionistPaymentPeriodService.createPaymentPeriod(paymentPeriodDTO);
        return new ResponseEntity<>(createdPeriod, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NutritionistPaymentPeriod>> getAllPaymentPeriods() {
        List<NutritionistPaymentPeriod> periods = nutritionistPaymentPeriodService.getAllPaymentPeriods();
        return ResponseEntity.ok(periods);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<NutritionistPaymentPeriod> getPaymentPeriodById(@PathVariable Integer id) {
        NutritionistPaymentPeriod period = nutritionistPaymentPeriodService.getPaymentPeriodById(id);
        return ResponseEntity.ok(period);
    }

    @PutMapping("/{id}/process")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NutritionistPaymentPeriod> processPayment(@PathVariable Integer id) {
        NutritionistPaymentPeriod processedPeriod = nutritionistPaymentPeriodService.processPayment(id);
        return ResponseEntity.ok(processedPeriod);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NutritionistPaymentPeriod> updatePaymentPeriod(
            @PathVariable Integer id,
            @Valid @RequestBody NutritionistPaymentPeriodDTO paymentPeriodDTO) {
        NutritionistPaymentPeriod updatedPeriod = nutritionistPaymentPeriodService.updatePaymentPeriod(id, paymentPeriodDTO);
        return ResponseEntity.ok(updatedPeriod);
    }
}