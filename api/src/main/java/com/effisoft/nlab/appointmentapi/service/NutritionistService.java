package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.NutritionistDTO;
import com.effisoft.nlab.appointmentapi.entity.Nutritionist;
import com.effisoft.nlab.appointmentapi.exception.NutritionistServiceException;
import com.effisoft.nlab.appointmentapi.repository.NutritionistRepository;
import com.effisoft.nlab.appointmentapi.service.base.ServiceExceptionHandler;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> {
                    // Validate email uniqueness
                    if (nutritionistRepository.findByEmail(dto.getEmail().toLowerCase()).isPresent()) {
                        throw new NutritionistServiceException(
                                String.format("Nutritionist with email %s already exists", dto.getEmail()));
                    }

                    // Create and sanitize new nutritionist
                    Nutritionist nutritionist = new Nutritionist();
                    updateNutritionistFromDTO(nutritionist, dto);
                    nutritionist.setCreatedAt(LocalDateTime.now());
                    nutritionist.setActive(true);

                    return nutritionistRepository.save(nutritionist);
                },
                NutritionistServiceException::new,
                "Create Nutritionist");
    }

    @Transactional
    public Nutritionist updateNutritionist(Integer id, @Valid NutritionistDTO dto) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> {
                    Nutritionist existingNutritionist = nutritionistRepository.findById(id)
                            .orElseThrow(
                                    () -> new NutritionistServiceException("Nutritionist not found with id: " + id));

                    // Check email uniqueness if it's being changed
                    if (!existingNutritionist.getEmail().equals(dto.getEmail())) {
                        if (nutritionistRepository.findByEmail(dto.getEmail().toLowerCase()).isPresent()) {
                            throw new NutritionistServiceException("Email already in use: " + dto.getEmail());
                        }
                    }

                    updateNutritionistFromDTO(existingNutritionist, dto);
                    return nutritionistRepository.save(existingNutritionist);
                },
                NutritionistServiceException::new,
                "Update Nutritionist");
    }

    @Transactional(readOnly = true)
    public List<Nutritionist> getAllActiveNutritionists() {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> nutritionistRepository.findByActiveTrue(),
                NutritionistServiceException::new,
                "Get All Active Nutritionists");
    }

    @Transactional
    public Nutritionist deactivateNutritionist(Integer id) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> {
                    Nutritionist nutritionist = nutritionistRepository.findById(id)
                            .orElseThrow(
                                    () -> new NutritionistServiceException("Nutritionist not found with id: " + id));

                    nutritionist.setActive(false);
                    return nutritionistRepository.save(nutritionist);
                },
                NutritionistServiceException::new,
                "Deactivate Nutritionist");
    }

    private void updateNutritionistFromDTO(Nutritionist nutritionist, NutritionistDTO dto) {
        nutritionist.setFirstName(HtmlUtils.htmlEscape(dto.getFirstName().trim()));
        nutritionist.setLastName(HtmlUtils.htmlEscape(dto.getLastName().trim()));
        nutritionist.setEmail(dto.getEmail().toLowerCase().trim());
        nutritionist.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);
    }
}