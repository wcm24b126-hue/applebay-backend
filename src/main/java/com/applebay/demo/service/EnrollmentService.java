package com.applebay.demo.service;

import com.applebay.demo.dto.*;
import com.applebay.demo.entity.*;
import com.applebay.demo.exception.*;
import com.applebay.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public EnrollmentResponse enroll(EnrollmentRequest req) {
        Student student = studentRepository.findById(req.studentId()).orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Course course = courseRepository.findById(req.courseId()).orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!course.isActive()) throw new BusinessRuleException("Course is not active");
        String batch = req.batch().trim().toUpperCase();
        if (enrollmentRepository.existsByStudentIdAndCourseIdAndBatchIgnoreCase(student.getId(), course.getId(), batch)) {
            throw new DuplicateResourceException("Student already enrolled in this course and batch");
        }

        Enrollment e = new Enrollment(); e.setStudent(student); e.setCourse(course); e.setBatch(batch);
        return toResponse(enrollmentRepository.save(e));
    }

    public List<EnrollmentResponse> findByStudent(Long studentId) {
        return enrollmentRepository.findByStudentIdWithDetails(studentId).stream().map(this::toResponse).toList();
    }

    private EnrollmentResponse toResponse(Enrollment e) {
        return new EnrollmentResponse(e.getId(), e.getStudent().getId(), e.getStudent().getStudentNumber(), e.getStudent().getFirstName() + " " + e.getStudent().getLastName(), e.getCourse().getId(), e.getCourse().getCode(), e.getCourse().getName(), e.getBatch(), e.getEnrolledOn());
    }
}
