package com.applebay.demo.controller;
import com.applebay.demo.dto.*;
import com.applebay.demo.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class PaymentController {
    private final PaymentService service;
    @PostMapping("/payments") @ResponseStatus(HttpStatus.CREATED) public PaymentResponse record(@Valid @RequestBody PaymentRequest r) { return service.record(r.enrollmentId(), r.amount(), r.paymentDate(), r.method()); }
    @GetMapping("/enrollments/{id}/payments") public List<PaymentResponse> history(@PathVariable Long id) { return service.history(id); }
    @GetMapping("/enrollments/{id}/balance") public BalanceResponse balance(@PathVariable Long id) { return service.balance(id); }
}
