package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.OrderDTO;
import org.example.hondasupercub.dto.SellerDashboardDTO;

import java.util.List;

public interface SellerDashboardService {

    SellerDashboardDTO getSellerDashboardData(String authorizationHeader);
}