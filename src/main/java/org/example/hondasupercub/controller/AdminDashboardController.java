package org.example.hondasupercub.controller;

import org.example.hondasupercub.dto.AdminDashboardDTO;
import org.example.hondasupercub.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admindashboard")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService dashboardService;

    @GetMapping("/data")
    public ResponseEntity<AdminDashboardDTO> getDashboardData() {
        AdminDashboardDTO data = dashboardService.getDashboardData();
        return ResponseEntity.ok(data);
    }
}