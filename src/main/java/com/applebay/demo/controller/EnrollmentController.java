package com.applebay.demo.controller;
import com.applebay.demo.dto.*;
import com.applebay.demo.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/enrollments") @RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService service;
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public EnrollmentResponse enroll(@Valid @RequestBody EnrollmentRequest r) { return service.enroll(r); }
}
