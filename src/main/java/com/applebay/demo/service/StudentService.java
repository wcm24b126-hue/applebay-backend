package com.applebay.demo.service;

import com.applebay.demo.dto.*;
import com.applebay.demo.entity.Student;
import com.applebay.demo.exception.DuplicateResourceException;
import com.applebay.demo.exception.ResourceNotFoundException;
import com.applebay.demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {
    private final StudentRepository studentRepository;

    @Transactional
    public StudentResponse register(StudentRequest req) {
        if (studentRepository.existsByNic(req.nic())) throw new DuplicateResourceException("NIC already registered");
        if (studentRepository.existsByEmailIgnoreCase(req.email())) throw new DuplicateResourceException("Email already registered");

        Student s = new Student();
        s.setStudentNumber("AB" + Year.now().getValue() + "-" + String.format("%04d", studentRepository.nextStudentSequence()));
        s.setFirstName(req.firstName().trim()); s.setLastName(req.lastName().trim());
        s.setNic(req.nic().trim()); s.setEmail(req.email().trim().toLowerCase());
        s.setPhone(req.phone()); s.setAddress(req.address().trim()); s.setDateOfBirth(req.dateOfBirth());
        
        return toResponse(studentRepository.save(s));
    }

    public PageResponse<StudentResponse> search(String q, int page, int size) {
        String term = (q == null) ? "" : q.trim().toLowerCase();
        return PageResponse.of(studentRepository.search(term, PageRequest.of(Math.max(page, 0), Math.min(size, 100), Sort.by("studentNumber"))).map(this::toResponse));
    }

    public StudentResponse findById(Long id) {
        return toResponse(studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student not found")));
    }

    private StudentResponse toResponse(Student s) {
        return new StudentResponse(s.getId(), s.getStudentNumber(), s.getFirstName(), s.getLastName(), s.getNic(), s.getEmail(), s.getPhone(), s.getAddress(), s.getDateOfBirth());
    }
}
