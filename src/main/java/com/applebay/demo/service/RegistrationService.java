package com.applebay.demo.service;

import com.applebay.demo.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final StudentService studentService;
    private final EnrollmentService enrollmentService;
    private final PaymentService paymentService;

    @Transactional
    public RegistrationResponse registerAndEnroll(RegistrationRequest req) {
        StudentResponse student = studentService.register(req.student());
        EnrollmentResponse enrollment = enrollmentService.enroll(new EnrollmentRequest(student.id(), req.courseId(), req.batch()));
        FirstPaymentRequest fp = req.firstPayment();
        PaymentResponse payment = paymentService.record(enrollment.id(), fp.amount(), fp.paymentDate(), fp.method());
        return new RegistrationResponse(student, enrollment, payment);
    }
}
