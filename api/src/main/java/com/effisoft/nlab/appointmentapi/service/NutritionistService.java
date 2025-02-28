package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.NutritionistDTO;
import com.effisoft.nlab.appointmentapi.entity.Nutritionist;
import com.effisoft.nlab.appointmentapi.exception.NutritionistServiceException;
import com.effisoft.nlab.appointmentapi.repository.NutritionistRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class NutritionistService {
    private final NutritionistRepository nutritionistRepository;

    @Transactional
    public Nutritionist createNutritionist(@Valid NutritionistDTO dto) {
        try {
            // Validate email uniqueness
            if (nutritionistRepository.findByEmail(dto.getEmail().toLowerCase()).isPresent()) {
                throw new NutritionistServiceException(
                        String.format("Nutritionist with email %s already exists", dto.getEmail())
                );
            }

            // Create and sanitize new nutritionist
            Nutritionist nutritionist = new Nutritionist();
            updateNutritionistFromDTO(nutritionist, dto);
            nutritionist.setCreatedAt(LocalDateTime.now());
            nutritionist.setActive(true);

            return nutritionistRepository.save(nutritionist);
        } catch (NutritionistServiceException e) {
            if(e.getMessage().equals(String.format("Nutritionist with email %s already exists", dto.getEmail()))){
                throw e;
            } else {
                throw new NutritionistServiceException("Unexpected error while creating nutritionist", e);
            }
        } catch (DataIntegrityViolationException e) {
            throw new NutritionistServiceException("Failed to create nutritionist due to data integrity violation", e);
        } catch (Exception e) {
            throw new NutritionistServiceException("Unexpected error while creating nutritionist", e);
        }
    }

    @Transactional
    public Nutritionist updateNutritionist(Integer id, @Valid NutritionistDTO dto) {
        try {
            Nutritionist existingNutritionist = nutritionistRepository.findById(id)
                    .orElseThrow(() -> new NutritionistServiceException("Nutritionist not found with id: " + id));

            // Check email uniqueness if it's being changed
            if (!existingNutritionist.getEmail().equals(dto.getEmail())) {
                if (nutritionistRepository.findByEmail(dto.getEmail().toLowerCase()).isPresent()) {
                    throw new NutritionistServiceException("Email already in use: " + dto.getEmail());
                }
            }

            updateNutritionistFromDTO(existingNutritionist, dto);
            return nutritionistRepository.save(existingNutritionist);
        } catch (DataIntegrityViolationException e) {
            throw new NutritionistServiceException("Failed to update nutritionist due to data integrity violation", e);
        }
    }

    private void updateNutritionistFromDTO(Nutritionist nutritionist, NutritionistDTO dto) {
        nutritionist.setFirstName(HtmlUtils.htmlEscape(dto.getFirstName().trim()));
        nutritionist.setLastName(HtmlUtils.htmlEscape(dto.getLastName().trim()));
        nutritionist.setEmail(dto.getEmail().toLowerCase().trim());
        nutritionist.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);
    }

    @Transactional(readOnly = true)
    public List<Nutritionist> getAllActiveNutritionists() {
        return nutritionistRepository.findByActiveTrue();
    }

    @Transactional
    public void deactivateNutritionist(Integer id) {
        try {
            Nutritionist nutritionist = nutritionistRepository.findById(id)
                    .orElseThrow(() -> new NutritionistServiceException("Nutritionist not found with id: " + id));

            nutritionist.setActive(false);
            nutritionistRepository.save(nutritionist);
        } catch (NutritionistServiceException nsEx) {
            if(nsEx.getMessage().equals("Nutritionist not found with id: " + id)) {
                throw nsEx;
            } else {
                throw new NutritionistServiceException("Unexpected error while creating nutritionist", nsEx);
            }
        } catch (Exception e) {
            throw new NutritionistServiceException("Failed to deactivate nutritionist", e);
        }
    }
}