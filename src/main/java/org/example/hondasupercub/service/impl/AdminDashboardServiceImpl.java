package org.example.hondasupercub.service.impl;

import org.example.hondasupercub.dto.AdminDashboardDTO;

import org.example.hondasupercub.repo.AdminOrderRepo;
import org.example.hondasupercub.repo.AdminSparePartRepo;
import org.example.hondasupercub.repo.AdminTransactionRepo;
import org.example.hondasupercub.repo.UserRepository;
import org.example.hondasupercub.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AdminOrderRepo orderRepo;

    @Autowired
    private AdminSparePartRepo sparePartRepo;

    @Autowired
    private AdminTransactionRepo transactionRepo;

    @Override
    public AdminDashboardDTO getDashboardData() {
        AdminDashboardDTO dto = new AdminDashboardDTO();
        dto.setTotalUsers(userRepo.count());
        dto.setTotalOrders(orderRepo.count());
        dto.setTotalSpareParts(sparePartRepo.count());
        dto.setTotalTransactions(transactionRepo.sumTransactions());
        return dto;
    }
}