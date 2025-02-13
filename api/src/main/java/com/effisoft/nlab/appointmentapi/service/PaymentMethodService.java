package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.PaymentMethodDTO;
import com.effisoft.nlab.appointmentapi.entity.PaymentMethod;
import com.effisoft.nlab.appointmentapi.exception.PaymentMethodServiceException;
import com.effisoft.nlab.appointmentapi.repository.PaymentMethodRepository;
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
public class PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;

    @Transactional
    public PaymentMethod createPaymentMethod(@Valid PaymentMethodDTO dto) {
        try {
            PaymentMethod paymentMethod = new PaymentMethod();
            updatePaymentMethodFromDTO(paymentMethod, dto);
            return paymentMethodRepository.save(paymentMethod);
        } catch (DataIntegrityViolationException e) {
            throw new PaymentMethodServiceException("Failed to create payment method due to data integrity violation", e);
        } catch (Exception e) {
            throw new PaymentMethodServiceException("Unexpected error while creating payment method", e);
        }
    }

    @Transactional(readOnly = true)
    public List<PaymentMethod> getAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PaymentMethod getPaymentMethodById(Integer id) {
        return paymentMethodRepository.findById(id)
                .orElseThrow(() -> new PaymentMethodServiceException("Payment method not found with id: " + id));
    }

    @Transactional
    public PaymentMethod updatePaymentMethod(Integer id, @Valid PaymentMethodDTO dto) {
        try {
            PaymentMethod existingPaymentMethod = getPaymentMethodById(id);
            updatePaymentMethodFromDTO(existingPaymentMethod, dto);
            return paymentMethodRepository.save(existingPaymentMethod);
        } catch (DataIntegrityViolationException e) {
            throw new PaymentMethodServiceException("Failed to update payment method due to data integrity violation", e);
        }
    }

    @Transactional
    public void deletePaymentMethod(Integer id) {
        try {
            PaymentMethod paymentMethod = getPaymentMethodById(id);
            paymentMethodRepository.delete(paymentMethod);
        } catch (Exception e) {
            throw new PaymentMethodServiceException("Failed to delete payment method", e);
        }
    }

    private void updatePaymentMethodFromDTO(PaymentMethod paymentMethod, PaymentMethodDTO dto) {
        paymentMethod.setName(HtmlUtils.htmlEscape(dto.getName().trim()));
        if (dto.getDescription() != null) {
            paymentMethod.setDescription(HtmlUtils.htmlEscape(dto.getDescription().trim()));
        }
    }
}