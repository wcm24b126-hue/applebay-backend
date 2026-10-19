package com.applebay.demo.controller;
import com.applebay.demo.dto.DashboardResponse;
import com.applebay.demo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/dashboard") @RequiredArgsConstructor
public class DashboardController {
    private final DashboardService service;
    @GetMapping public DashboardResponse summary() { return service.summary(); }
}
