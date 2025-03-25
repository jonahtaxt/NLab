package com.effisoft.nlab.appointmentapi.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.effisoft.nlab.appointmentapi.dto.PatientPaymentDTO;
import com.effisoft.nlab.appointmentapi.entity.CardPaymentType;
import com.effisoft.nlab.appointmentapi.entity.PackageType;
import com.effisoft.nlab.appointmentapi.entity.PatientPayment;
import com.effisoft.nlab.appointmentapi.entity.PaymentMethod;
import com.effisoft.nlab.appointmentapi.entity.PurchasedPackage;
import com.effisoft.nlab.appointmentapi.exception.PatientPaymentException;
import com.effisoft.nlab.appointmentapi.repository.CardPaymentTypeRepository;
import com.effisoft.nlab.appointmentapi.repository.PackageTypeRepository;
import com.effisoft.nlab.appointmentapi.repository.PatientPaymentRepository;
import com.effisoft.nlab.appointmentapi.repository.PaymentMethodRepository;
import com.effisoft.nlab.appointmentapi.repository.PurchasedPackageRepository;
import com.effisoft.nlab.appointmentapi.service.base.ServiceExceptionHandler;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class PatientPaymentService {
    private final PatientPaymentRepository patientPaymentRepository;
    private final PurchasedPackageRepository purchasedPackageRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final CardPaymentTypeRepository cardPaymentTypeRepository;
    private final PackageTypeRepository packageTypeRepository;
    

    @Transactional
    public PatientPayment createPatientPayment(@Valid PatientPaymentDTO dto) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
            () -> {
                PatientPayment patientPayment = new PatientPayment();
                CardPaymentType cardPaymentType = null;

                //Set related entities
                PurchasedPackage purchasedPackage = purchasedPackageRepository.findById(dto.getPurchasedPackageId())
                    .orElseThrow(() -> new PatientPaymentException("Purchased Package not found"));

                PaymentMethod paymentMethod = paymentMethodRepository.findById(dto.getPaymentMethodId())
                    .orElseThrow(() -> new PatientPaymentException("Payment Method not found"));

                if(!paymentMethod.getName().toLowerCase().equals("efectivo")) {
                    cardPaymentType = cardPaymentTypeRepository.findById(dto.getCardPaymentTypeId())
                    .orElseThrow(() -> new PatientPaymentException("Card Payment Type not found"));
                }

                PackageType packageType = packageTypeRepository.findById(purchasedPackage.getId())
                    .orElseThrow(() -> new PatientPaymentException("Package Type not found"));

                if(!(packageType.getPrice().compareTo(dto.getTotalPaid()) == -1)) {
                    purchasedPackage.setPaidInFull(true);
                    purchasedPackageRepository.save(purchasedPackage);
                }

                patientPayment.setPurchasedPackage(purchasedPackage);
                patientPayment.setPaymentMethod(paymentMethod);
                patientPayment.setCardPaymentType(cardPaymentType);
                patientPayment.setTotalPaid(dto.getTotalPaid());
                patientPayment.setPaymentDate(LocalDateTime.now());

                return patientPaymentRepository.save(patientPayment);

            }, PatientPaymentException::new, "Create Patient Payment");
    }
}
