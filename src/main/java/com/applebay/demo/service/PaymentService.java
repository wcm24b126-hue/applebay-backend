package com.applebay.demo.service;

import com.applebay.demo.dto.*;
import com.applebay.demo.entity.*;
import com.applebay.demo.exception.*;
import com.applebay.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Transactional
    public PaymentResponse record(Long enrollmentId, BigDecimal amount, LocalDate date, PaymentMethod method) {
        Enrollment enrollment = enrollmentRepository.findByIdForUpdate(enrollmentId).orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
        BigDecimal fee = enrollment.getCourse().getTotalFee();
        BigDecimal paid = paymentRepository.sumByEnrollmentId(enrollmentId);
        BigDecimal outstanding = fee.subtract(paid);

        if (outstanding.signum() == 0) throw new BusinessRuleException("Enrollment is fully paid");
        if (amount.compareTo(outstanding) > 0) throw new BusinessRuleException("Payment exceeds balance");

        Payment p = new Payment(); p.setEnrollment(enrollment);
        p.setReceiptNumber("RC" + Year.now().getValue() + "-" + String.format("%06d", paymentRepository.nextReceiptSequence()));
        p.setAmount(amount); p.setPaymentDate(date != null ? date : LocalDate.now()); p.setMethod(method);
        return toResponse(paymentRepository.save(p));
    }

    public List<PaymentResponse> history(Long enrollmentId) {
        return paymentRepository.findByEnrollmentIdOrderByPaymentDateDescIdDesc(enrollmentId).stream().map(this::toResponse).toList();
    }

    public BalanceResponse balance(Long enrollmentId) {
        Enrollment e = enrollmentRepository.findDetailsById(enrollmentId).orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
        BigDecimal fee = e.getCourse().getTotalFee();
        BigDecimal paid = paymentRepository.sumByEnrollmentId(enrollmentId);
        return new BalanceResponse(e.getId(), e.getStudent().getStudentNumber(), e.getStudent().getFirstName() + " " + e.getStudent().getLastName(), e.getCourse().getCode(), e.getCourse().getName(), fee, paid, fee.subtract(paid));
    }

    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(p.getId(), p.getReceiptNumber(), p.getEnrollment().getId(), p.getAmount(), p.getPaymentDate(), p.getMethod());
    }
}
