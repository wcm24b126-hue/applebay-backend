package com.applebay.demo.controller;

import com.applebay.demo.dto.*;
import com.applebay.demo.entity.Enrollment;
import com.applebay.demo.entity.Student;
import com.applebay.demo.exception.ResourceNotFoundException;
import com.applebay.demo.repository.EnrollmentRepository;
import com.applebay.demo.repository.StudentRepository;
import com.applebay.demo.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService service;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    @PostMapping("/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse record(@Valid @RequestBody PaymentRequest r) {
        return service.record(r.enrollmentId(), r.amount(), r.paymentDate(), r.method());
    }

    @GetMapping("/enrollments/{id}/payments")
    public List<PaymentResponse> history(@PathVariable Long id) {
        return service.history(id);
    }

    @GetMapping("/enrollments/{id}/balance")
    public BalanceResponse balance(@PathVariable Long id) {
        return service.balance(id);
    }

    // Flexible text lookup: supports Student Number (AB2026-xxxx), NIC, or numeric ID
    @GetMapping("/enrollments/lookup")
    public BalanceResponse lookup(@RequestParam String query) {
        String q = query.trim();
        
        // 1. Try by numeric enrollment ID directly if strictly digits
        if (q.matches("^[0-9]+$")) {
            try {
                Long enrollId = Long.parseLong(q);
                return service.balance(enrollId);
            } catch (Exception ignored) {}
        }

        // 2. Search student by studentNumber or NIC
        var students = studentRepository.search(q.toLowerCase(), PageRequest.of(0, 5));
        if (students.isEmpty()) {
            throw new ResourceNotFoundException("No student found matching: " + q);
        }

        Student s = students.getContent().get(0);
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdWithDetails(s.getId());
        if (enrollments.isEmpty()) {
            throw new ResourceNotFoundException("Student " + s.getStudentNumber() + " has no active course enrollments.");
        }

        return service.balance(enrollments.get(0).getId());
    }
}
