package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.entity.Nutritionist;
import com.effisoft.nlab.appointmentapi.repository.NutritionistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NutritionistService {
    private final NutritionistRepository nutritionistRepository;

    @Transactional
    public Nutritionist createNutritionist(Nutritionist nutritionist) {
        // Validate email uniqueness
        if (nutritionistRepository.findByEmail(nutritionist.getEmail()).isPresent()) {
            throw new RuntimeException("Nutritionist with this email already exists");
        }

        nutritionist.setCreatedAt(LocalDateTime.now());
        nutritionist.setActive(true);
        return nutritionistRepository.save(nutritionist);
    }

    @Transactional(readOnly = true)
    public List<Nutritionist> getAllActiveNutritionists() {
        return nutritionistRepository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Nutritionist> getNutritionistById(Integer id) {
        return nutritionistRepository.findById(id);
    }

    @Transactional
    public Nutritionist updateNutritionist(Integer id, Nutritionist updatedNutritionist) {
        return nutritionistRepository.findById(id)
                .map(existingNutritionist -> {
                    existingNutritionist.setFirstName(updatedNutritionist.getFirstName());
                    existingNutritionist.setLastName(updatedNutritionist.getLastName());
                    existingNutritionist.setPhone(updatedNutritionist.getPhone());
                    return nutritionistRepository.save(existingNutritionist);
                })
                .orElseThrow(() -> new RuntimeException("Nutritionist not found"));
    }

    @Transactional
    public void deactivateNutritionist(Integer id) {
        Nutritionist nutritionist = nutritionistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nutritionist not found"));

        nutritionist.setActive(false);
        nutritionistRepository.save(nutritionist);
    }
}
