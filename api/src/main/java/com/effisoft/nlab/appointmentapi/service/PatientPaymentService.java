package com.effisoft.nlab.appointmentapi.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.effisoft.nlab.appointmentapi.dto.PatientPaymentDTO;
import com.effisoft.nlab.appointmentapi.dto.PatientPurchasedPackageDTO;
import com.effisoft.nlab.appointmentapi.dto.PurchasedPackageDTO;
import com.effisoft.nlab.appointmentapi.entity.CardPaymentType;
import com.effisoft.nlab.appointmentapi.entity.PatientPayment;
import com.effisoft.nlab.appointmentapi.entity.PaymentMethod;
import com.effisoft.nlab.appointmentapi.entity.PurchasedPackage;
import com.effisoft.nlab.appointmentapi.exception.PatientPaymentException;
import com.effisoft.nlab.appointmentapi.mapper.PurchasedPackageMapper;
import com.effisoft.nlab.appointmentapi.repository.CardPaymentTypeRepository;
import com.effisoft.nlab.appointmentapi.repository.PatientPaymentRepository;
import com.effisoft.nlab.appointmentapi.repository.PaymentMethodRepository;
import com.effisoft.nlab.appointmentapi.service.base.ServiceExceptionHandler;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class PatientPaymentService {
    private final PatientPaymentRepository patientPaymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final CardPaymentTypeRepository cardPaymentTypeRepository;
    private final PurchasedPackageService purchasedPackageService;
    private final PurchasedPackageMapper purchasedPackageMapper;

    @Transactional
    public PatientPayment createPatientPayment(@Valid PatientPaymentDTO dto) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> {
                    PatientPayment patientPayment = new PatientPayment();
                    CardPaymentType cardPaymentType = null;

                    PatientPurchasedPackageDTO patientPurchasedPackageDTO = purchasedPackageService
                            .getPatientPurchasedPackageByPackageId(dto.getPurchasedPackageId());

                    if(patientPurchasedPackageDTO == null) {
                        throw new PatientPaymentException("Purchased Package not found");
                    }

                    PurchasedPackage purchasedPackage = patientPurchasedPackageDTO.getPurchasedPackage();

                    PaymentMethod paymentMethod = paymentMethodRepository.findById(dto.getPaymentMethodId())
                            .orElseThrow(() -> new PatientPaymentException("Payment Method not found"));

                    if (!paymentMethod.getName().toLowerCase().equals("efectivo")) {
                        cardPaymentType = cardPaymentTypeRepository.findById(dto.getCardPaymentTypeId())
                                .orElseThrow(() -> new PatientPaymentException("Card Payment Type not found"));
                    }

                    BigDecimal packageTotalPaid = patientPurchasedPackageDTO.getPackagePaidTotal().add(dto.getTotalPaid());

                    if(packageTotalPaid.compareTo(purchasedPackage.getPackageType().getPrice()) == 1) {
                        throw new PatientPaymentException("El pago excede el total del paquete");
                    } else if (packageTotalPaid.compareTo(purchasedPackage.getPackageType().getPrice()) == 0) {
                        purchasedPackage.setPaidInFull(true);
                        PurchasedPackageDTO purchasedPackageDTO = purchasedPackageMapper.toDto(purchasedPackage);
                        purchasedPackageDTO.setPatientId(purchasedPackage.getPatient().getId());
                        purchasedPackageDTO.setPackageTypeId(purchasedPackage.getPackageType().getId());
                        purchasedPackageService.updatePurchasedPackage(purchasedPackageDTO.getId(), purchasedPackageDTO);
                    }

                    patientPayment.setPurchasedPackage(purchasedPackage);
                    patientPayment.setPaymentMethod(paymentMethod);
                    patientPayment.setCardPaymentType(cardPaymentType);
                    patientPayment.setTotalPaid(dto.getTotalPaid());
                    patientPayment.setPaymentDate(LocalDateTime.now());

                    return patientPaymentRepository.save(patientPayment);

                }, PatientPaymentException::new, "Create Patient Payment");
    }

    @Transactional
    public List<PatientPayment> getPatientPaymentsByPurchasedPackageId(Integer purchasedPackageId) {
        return ServiceExceptionHandler.executeWithExceptionHandling(
                () -> {
                    List<PatientPayment> patientPayments = patientPaymentRepository
                            .findByPurchasedPackageId(purchasedPackageId);

                    return patientPayments;
                }, PatientPaymentException::new, "Get Patient Payments by Purchased Package ID");
    }
}
