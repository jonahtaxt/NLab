package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.CardPaymentTypeDTO;
import com.effisoft.nlab.appointmentapi.entity.CardPaymentType;
import com.effisoft.nlab.appointmentapi.exception.CardPaymentTypeServiceException;
import com.effisoft.nlab.appointmentapi.repository.CardPaymentTypeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class CardPaymentTypeService {
    private final CardPaymentTypeRepository cardPaymentTypeRepository;

    @Transactional
    public CardPaymentType createCardPaymentType(@Valid CardPaymentTypeDTO dto) {
        try {
            CardPaymentType cardPaymentType = new CardPaymentType();
            updateCardPaymentTypeFromDTO(cardPaymentType, dto);
            cardPaymentType.setActive(true);

            return cardPaymentTypeRepository.save(cardPaymentType);
        } catch (DataIntegrityViolationException e) {
            throw new CardPaymentTypeServiceException(
                    "Failed to create card payment type due to data integrity violation", e);
        }
    }

    @Transactional(readOnly = true)
    public List<CardPaymentType> getAllActiveCardPaymentTypes() {
        return cardPaymentTypeRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public CardPaymentType getCardPaymentTypeById(Integer id) {
        return cardPaymentTypeRepository.findById(id)
                .orElseThrow(() -> new CardPaymentTypeServiceException(
                        "Card payment type not found with id: " + id));
    }

    @Transactional
    public CardPaymentType updateCardPaymentType(Integer id, @Valid CardPaymentTypeDTO dto) {
        try {
            CardPaymentType existingType = getCardPaymentTypeById(id);
            updateCardPaymentTypeFromDTO(existingType, dto);
            return cardPaymentTypeRepository.save(existingType);
        } catch (DataIntegrityViolationException e) {
            throw new CardPaymentTypeServiceException(
                    "Failed to update card payment type due to data integrity violation", e);
        }
    }

    @Transactional
    public void deactivateCardPaymentType(Integer id) {
        try {
            CardPaymentType cardPaymentType = getCardPaymentTypeById(id);
            cardPaymentType.setActive(false);
            cardPaymentTypeRepository.save(cardPaymentType);
        } catch (Exception e) {
            throw new CardPaymentTypeServiceException("Failed to deactivate card payment type", e);
        }
    }

    private void updateCardPaymentTypeFromDTO(CardPaymentType cardPaymentType, CardPaymentTypeDTO dto) {
        cardPaymentType.setName(HtmlUtils.htmlEscape(dto.getName().trim()));
        cardPaymentType.setDescription(dto.getDescription() != null ?
                HtmlUtils.htmlEscape(dto.getDescription().trim()) : null);
        cardPaymentType.setBankFeePercentage(dto.getBankFeePercentage());
        cardPaymentType.setNumberOfInstallments(dto.getNumberOfInstallments());
    }
}