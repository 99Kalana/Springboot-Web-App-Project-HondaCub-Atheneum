//SellerDashboardDTO.java
package org.example.hondasupercub.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class SellerDashboardDTO {
    private long totalProducts;
    private long pendingOrders;
    private double totalSales;
    private List<OrderDTO> orders;
}