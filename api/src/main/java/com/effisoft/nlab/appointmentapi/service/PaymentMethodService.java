package com.effisoft.nlab.appointmentapi.service;

import com.effisoft.nlab.appointmentapi.entity.PaymentMethod;
import com.effisoft.nlab.appointmentapi.exception.PaymentMethodServiceException;
import com.effisoft.nlab.appointmentapi.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;

    @Transactional(readOnly = true)
    public List<PaymentMethod> getAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<PaymentMethod> getPaymentMethodById(Integer id) {
        return paymentMethodRepository.findById(id);
    }

    @Transactional
    public PaymentMethod createPaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethodRepository.save(paymentMethod);
    }

    @Transactional
    public PaymentMethod updatePaymentMethod(Integer id, PaymentMethod updatedPaymentMethod) {
        return paymentMethodRepository.findById(id)
                .map(existingMethod -> {
                    existingMethod.setName(updatedPaymentMethod.getName());
                    existingMethod.setDescription(updatedPaymentMethod.getDescription());
                    return paymentMethodRepository.save(existingMethod);
                })
                .orElseThrow(() -> new PaymentMethodServiceException("Payment Method not found"));
    }

    @Transactional
    public void deletePaymentMethod(Integer id) {
        paymentMethodRepository.deleteById(id);
    }
}
