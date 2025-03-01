package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.dto.PaymentMethodDTO;
import com.effisoft.nlab.appointmentapi.entity.PaymentMethod;
import com.effisoft.nlab.appointmentapi.exception.PaymentMethodServiceException;
import com.effisoft.nlab.appointmentapi.repository.PaymentMethodRepository;
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
public class PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;

    @Transactional
    public PaymentMethod createPaymentMethod(@Valid PaymentMethodDTO dto) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> {
                    PaymentMethod paymentMethod = new PaymentMethod();
                    updatePaymentMethodFromDTO(paymentMethod, dto);
                    return paymentMethodRepository.save(paymentMethod);
                },
                PaymentMethodServiceException::new,
                "Create Payment Method");
    }

    @Transactional(readOnly = true)
    public List<PaymentMethod> getAllPaymentMethods() {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                paymentMethodRepository::findAll,
                PaymentMethodServiceException::new,
                "Get All Payment Methods");
    }

    @Transactional(readOnly = true)
    public PaymentMethod getPaymentMethodById(Integer id) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> paymentMethodRepository.findById(id)
                        .orElseThrow(
                                () -> new PaymentMethodServiceException("Payment method not found with id: " + id)),
                PaymentMethodServiceException::new,
                "Get Payment Method by Id");
    }

    @Transactional
    public PaymentMethod updatePaymentMethod(Integer id, @Valid PaymentMethodDTO dto) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> {
                    PaymentMethod existingPaymentMethod = getPaymentMethodById(id);
                    updatePaymentMethodFromDTO(existingPaymentMethod, dto);
                    return paymentMethodRepository.save(existingPaymentMethod);
                },
                PaymentMethodServiceException::new,
                "Update Payment Method");
    }

    @Transactional
    public boolean deletePaymentMethod(Integer id) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> deletePaymentMethodById(id),
                PaymentMethodServiceException::new,
                "Delete Payment Method");
    }

    private boolean deletePaymentMethodById(Integer id) {
        PaymentMethod paymentMethod = getPaymentMethodById(id);
        paymentMethodRepository.delete(paymentMethod);
        return true;
    }

    private void updatePaymentMethodFromDTO(PaymentMethod paymentMethod, PaymentMethodDTO dto) {
        paymentMethod.setName(HtmlUtils.htmlEscape(dto.getName().trim()));
        if (dto.getDescription() != null) {
            paymentMethod.setDescription(HtmlUtils.htmlEscape(dto.getDescription().trim()));
        }
    }
}