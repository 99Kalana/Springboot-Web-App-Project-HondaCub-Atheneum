// SellerDashboardController.java
package org.example.hondasupercub.controller;

import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.dto.SellerDashboardDTO;
import org.example.hondasupercub.service.SellerDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/seller/dashboard")
public class SellerDashboardController {

    @Autowired
    private SellerDashboardService sellerDashboardService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getSellerDashboardData(@RequestHeader("Authorization") String authorizationHeader) {
        SellerDashboardDTO dashboardData = sellerDashboardService.getSellerDashboardData(authorizationHeader);
        return new ResponseEntity<>(new ResponseDTO(HttpStatus.OK.value(), "Dashboard data fetched successfully", dashboardData), HttpStatus.OK);
    }
}