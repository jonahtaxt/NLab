package com.effisoft.nlab.appointmentapi.controller;

import com.effisoft.nlab.appointmentapi.dto.CardPaymentTypeDTO;
import com.effisoft.nlab.appointmentapi.entity.CardPaymentType;
import com.effisoft.nlab.appointmentapi.service.CardPaymentTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/card-payment-types")
@RequiredArgsConstructor
public class CardPaymentTypeController {
    private final CardPaymentTypeService cardPaymentTypeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardPaymentType> createCardPaymentType(
            @Valid @RequestBody CardPaymentTypeDTO cardPaymentTypeDTO) {
        CardPaymentType created = cardPaymentTypeService.createCardPaymentType(cardPaymentTypeDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<List<CardPaymentType>> getAllActiveCardPaymentTypes() {
        List<CardPaymentType> types = cardPaymentTypeService.getAllActiveCardPaymentTypes();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    public ResponseEntity<CardPaymentType> getCardPaymentTypeById(@PathVariable Integer id) {
        CardPaymentType type = cardPaymentTypeService.getCardPaymentTypeById(id);
        return ResponseEntity.ok(type);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardPaymentType> updateCardPaymentType(
            @PathVariable Integer id,
            @Valid @RequestBody CardPaymentTypeDTO cardPaymentTypeDTO) {
        CardPaymentType updated = cardPaymentTypeService.updateCardPaymentType(id, cardPaymentTypeDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateCardPaymentType(@PathVariable Integer id) {
        cardPaymentTypeService.deactivateCardPaymentType(id);
        return ResponseEntity.noContent().build();
    }
}