package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.CardPaymentTypeDTO;
import com.effisoft.nlab.appointmentapi.entity.CardPaymentType;
import com.effisoft.nlab.appointmentapi.exception.CardPaymentTypeServiceException;
import com.effisoft.nlab.appointmentapi.repository.CardPaymentTypeRepository;
import com.effisoft.nlab.appointmentapi.service.base.ServiceExceptionHandler;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> {
                CardPaymentType cardPaymentType = new CardPaymentType();
                updateCardPaymentTypeFromDTO(cardPaymentType, dto);
                cardPaymentType.setActive(true);
                return cardPaymentTypeRepository.save(cardPaymentType);
            },
            CardPaymentTypeServiceException::new,
            "Create Card Payment Type"
        );
    }

    @Transactional(readOnly = true)
    public List<CardPaymentType> getAllActiveCardPaymentTypes() {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> cardPaymentTypeRepository.findByActiveTrue(),
            CardPaymentTypeServiceException::new,
            "Get All Active Card Payment Types"
        );
    }

    @Transactional(readOnly = true)
    public CardPaymentType getCardPaymentTypeById(Integer id) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> cardPaymentTypeRepository.findById(id)
                .orElseThrow(() -> new CardPaymentTypeServiceException(
                    "Card payment type not found with id: " + id)),
            CardPaymentTypeServiceException::new,
            "Get Card Payment Type by ID"
        );
    }

    @Transactional
    public CardPaymentType updateCardPaymentType(Integer id, @Valid CardPaymentTypeDTO dto) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> {
                CardPaymentType existingType = getCardPaymentTypeById(id);
                updateCardPaymentTypeFromDTO(existingType, dto);
                return cardPaymentTypeRepository.save(existingType);
            },
            CardPaymentTypeServiceException::new,
            "Update Card Payment Type"
        );
    }

    @Transactional
    public CardPaymentType deactivateCardPaymentType(Integer id) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> {
                CardPaymentType cardPaymentType = getCardPaymentTypeById(id);
                cardPaymentType.setActive(false);
                return cardPaymentTypeRepository.save(cardPaymentType);
            },
            CardPaymentTypeServiceException::new,
            "Deactivate Card Payment Type"
        );
    }

    private void updateCardPaymentTypeFromDTO(CardPaymentType cardPaymentType, CardPaymentTypeDTO dto) {
        cardPaymentType.setName(HtmlUtils.htmlEscape(dto.getName().trim()));
        cardPaymentType.setDescription(dto.getDescription() != null ?
                HtmlUtils.htmlEscape(dto.getDescription().trim()) : null);
        cardPaymentType.setBankFeePercentage(dto.getBankFeePercentage());
        cardPaymentType.setNumberOfInstallments(dto.getNumberOfInstallments());
    }
}