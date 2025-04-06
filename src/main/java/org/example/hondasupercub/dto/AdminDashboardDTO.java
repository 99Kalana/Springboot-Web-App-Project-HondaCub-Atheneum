package org.example.hondasupercub.dto;

import lombok.Data;

@Data
public class AdminDashboardDTO {
    private long totalUsers;
    private long totalOrders;
    private long totalSpareParts;
    private double totalTransactions;
}