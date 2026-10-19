package com.applebay.demo.controller;
import com.applebay.demo.dto.*;
import com.applebay.demo.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/students") @RequiredArgsConstructor
public class StudentController {
    private final StudentService service; private final EnrollmentService enrollmentService;
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public StudentResponse register(@Valid @RequestBody StudentRequest r) { return service.register(r); }
    @GetMapping public PageResponse<StudentResponse> search(@RequestParam(required=false) String q, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size) { return service.search(q, page, size); }
    @GetMapping("/{id}") public StudentResponse getById(@PathVariable Long id) { return service.findById(id); }
    @GetMapping("/{id}/enrollments") public List<EnrollmentResponse> enrollments(@PathVariable Long id) { return enrollmentService.findByStudent(id); }
}
