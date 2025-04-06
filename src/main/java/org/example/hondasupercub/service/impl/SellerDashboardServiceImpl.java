package org.example.hondasupercub.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.hondasupercub.dto.OrderDTO;
import org.example.hondasupercub.dto.SellerDashboardDTO;
import org.example.hondasupercub.entity.Order;
import org.example.hondasupercub.repo.SellerOrderRepo;
import org.example.hondasupercub.repo.SellerTransactionRepo;
import org.example.hondasupercub.repo.SparePartRepo;
import org.example.hondasupercub.service.SellerDashboardService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SellerDashboardServiceImpl implements SellerDashboardService {

    @Autowired
    private SellerOrderRepo orderRepository;

    @Autowired
    private SparePartRepo sparePartRepository;

    @Autowired
    private SellerTransactionRepo transactionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public SellerDashboardDTO getSellerDashboardData(String authorizationHeader) {
        int sellerId = extractSellerIdFromToken(authorizationHeader);

        long totalProducts = sparePartRepository.countBySellerUserId(sellerId);
        long pendingOrders = orderRepository.countPendingOrdersBySellerId(sellerId);
        Double totalSales = transactionRepository.calculateTotalSalesBySellerId(sellerId);

        List<Order> orders = orderRepository.findOrdersBySellerId(sellerId);
        List<OrderDTO> orderDTOs = orders.stream()
                .map(order -> {
                    OrderDTO dto = modelMapper.map(order, OrderDTO.class);
                    // Populate fullName from the User entity
                    if (order.getUser() != null) {
                        dto.setFullName(order.getUser().getFullName());
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        SellerDashboardDTO dashboardDTO = new SellerDashboardDTO();
        dashboardDTO.setTotalProducts(totalProducts);
        dashboardDTO.setPendingOrders(pendingOrders);
        dashboardDTO.setTotalSales(totalSales != null ? totalSales : 0.0);
        dashboardDTO.setOrders(orderDTOs);

        return dashboardDTO;
    }

    private int extractSellerIdFromToken(String authorizationHeader) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authorizationHeader.substring(7)).getBody();
        return (Integer) claims.get("userId");
    }
}