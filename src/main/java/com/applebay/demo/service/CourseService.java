package com.applebay.demo.service;

import com.applebay.demo.dto.CourseRequest;
import com.applebay.demo.dto.CourseResponse;
import com.applebay.demo.entity.Course;
import com.applebay.demo.exception.DuplicateResourceException;
import com.applebay.demo.exception.ResourceNotFoundException;
import com.applebay.demo.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {
    private final CourseRepository courseRepository;

    @Transactional
    public CourseResponse create(CourseRequest req) {
        if (courseRepository.existsByCodeIgnoreCase(req.code())) {
            throw new DuplicateResourceException("Course code already exists: " + req.code());
        }
        Course c = new Course();
        apply(c, req);
        return toResponse(courseRepository.save(c));
    }

    public List<CourseResponse> findAll() {
        return courseRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CourseResponse findById(Long id) {
        return toResponse(courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course not found")));
    }

    @Transactional
    public CourseResponse update(Long id, CourseRequest req) {
        Course c = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (courseRepository.existsByCodeIgnoreCaseAndIdNot(req.code(), id)) {
            throw new DuplicateResourceException("Course code already exists: " + req.code());
        }
        apply(c, req);
        return toResponse(c);
    }

    @Transactional
    public CourseResponse deactivate(Long id) {
        Course c = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        c.setActive(false);
        return toResponse(c);
    }

    private void apply(Course c, CourseRequest r) {
        c.setCode(r.code().trim().toUpperCase()); c.setName(r.name().trim());
        c.setLevel(r.level()); c.setDurationMonths(r.durationMonths()); c.setTotalFee(r.totalFee());
    }

    private CourseResponse toResponse(Course c) {
        return new CourseResponse(c.getId(), c.getCode(), c.getName(), c.getLevel(), c.getDurationMonths(), c.getTotalFee(), c.isActive());
    }
}
