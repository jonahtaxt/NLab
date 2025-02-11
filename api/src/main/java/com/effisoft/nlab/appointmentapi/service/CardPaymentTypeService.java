package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.entity.CardPaymentType;
import com.effisoft.nlab.appointmentapi.exception.CardPaymentTypeServiceException;
import com.effisoft.nlab.appointmentapi.repository.CardPaymentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardPaymentTypeService {
    private final CardPaymentTypeRepository cardPaymentTypeRepository;

    @Transactional(readOnly = true)
    public List<CardPaymentType> getAllCardPaymentTypes() {
        return cardPaymentTypeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<CardPaymentType> getCardPaymentTypeById(Integer id) {
        return cardPaymentTypeRepository.findById(id);
    }

    @Transactional
    public CardPaymentType createCardPaymentType(CardPaymentType cardPaymentType) {
        cardPaymentType.setActive(true);
        return cardPaymentTypeRepository.save(cardPaymentType);
    }

    @Transactional
    public CardPaymentType updateCardPaymentType(Integer id, CardPaymentType updatedCardPaymentType) {
        return cardPaymentTypeRepository.findById(id)
                .map(existingType -> {
                    existingType.setName(updatedCardPaymentType.getName());
                    existingType.setDescription(updatedCardPaymentType.getDescription());
                    existingType.setBankFeePercentage(updatedCardPaymentType.getBankFeePercentage());
                    existingType.setNumberOfInstallments(updatedCardPaymentType.getNumberOfInstallments());
                    return cardPaymentTypeRepository.save(existingType);
                })
                .orElseThrow(() -> new CardPaymentTypeServiceException("Card Payment Type not found"));
    }

    @Transactional
    public void deactivateCardPaymentType(Integer id) {
        cardPaymentTypeRepository.findById(id)
                .map(existingType -> {
                    existingType.setActive(false);
                    return cardPaymentTypeRepository.save(existingType);
                })
                .orElseThrow(() -> new CardPaymentTypeServiceException("Card Payment Type not found"));
    }
}
