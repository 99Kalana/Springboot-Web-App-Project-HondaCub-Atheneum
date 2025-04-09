package org.example.hondasupercub.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.hondasupercub.dto.OrderDTO;
import org.example.hondasupercub.dto.OrderDetailDTO;
import org.example.hondasupercub.entity.Order;
import org.example.hondasupercub.entity.OrderDetail;
import org.example.hondasupercub.repo.SellerOrderDetailRepo;
import org.example.hondasupercub.repo.SellerOrderRepo;
import org.example.hondasupercub.service.SellerOrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SellerOrderServiceImpl implements SellerOrderService {

    @Autowired
    private SellerOrderRepo orderRepo;

    @Autowired
    private SellerOrderDetailRepo orderDetailRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${jwt.secret}")
    private String secretKey;

    private int extractSellerIdFromToken(String authorizationHeader) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authorizationHeader.substring(7)).getBody();
        return (Integer) claims.get("userId");
    }

    @Override
    public List<OrderDTO> getOrdersBySellerId(String authorizationHeader) {
        int sellerId = extractSellerIdFromToken(authorizationHeader);
        List<Order> orders = orderRepo.findOrdersBySellerId(sellerId);
        return orders.stream().map(order -> modelMapper.map(order, OrderDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersBySellerIdAndStatus(String authorizationHeader, String status) {
        int sellerId = extractSellerIdFromToken(authorizationHeader);
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            List<Order> orders = orderRepo.findOrdersBySellerIdAndOrderStatus(sellerId, orderStatus);
            return orders.stream().map(order -> modelMapper.map(order, OrderDTO.class)).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            // Handle invalid status value (optional: log error, return empty list, etc.)
            return List.of();
        }
    }

    @Override
    public Optional<OrderDTO> getOrderByOrderIdAndSellerId(int orderId, String authorizationHeader) {
        int sellerId = extractSellerIdFromToken(authorizationHeader);
        Optional<Order> order = orderRepo.findOrderByOrderIdAndSellerId(orderId, sellerId);
        return order.map(value -> modelMapper.map(value, OrderDTO.class));
    }


    @Override
    public OrderDTO updateOrderStatus(int orderId, Order.OrderStatus newStatus, String authorizationHeader) {
        int sellerId = extractSellerIdFromToken(authorizationHeader);
        Order order = orderRepo.findByIdWithOrderDetails(orderId).orElse(null);

        if (order != null) {
            boolean orderBelongsToSeller = order.getOrderDetails().stream()
                    .anyMatch(detail -> detail.getSparePart().getSeller().getUserId() == sellerId);

            if (orderBelongsToSeller) {
                order.setOrderStatus(newStatus);
                Order updatedOrder = orderRepo.save(order);
                return modelMapper.map(updatedOrder, OrderDTO.class);
            }
            return null;
        }
        return null;
    }

    @Override
    public List<OrderDetailDTO> getOrderDetailsByOrderId(int orderId, String authorizationHeader) {
        int sellerId = extractSellerIdFromToken(authorizationHeader);
        Order order = orderRepo.findOrderByOrderIdAndSellerId(orderId, sellerId).orElse(null);

        if (order != null) {
            return order.getOrderDetails().stream()
                    .map(this::mapOrderDetailToDTO)
                    .collect(Collectors.toList());
        }
        return null;
    }

    private OrderDetailDTO mapOrderDetailToDTO(OrderDetail orderDetail) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setOrderDetailId(orderDetail.getOrderDetailId());
        dto.setOrderId(orderDetail.getOrder().getOrderId());
        dto.setSparePartId(orderDetail.getSparePart().getPartId());
        dto.setQuantity(orderDetail.getQuantity());
        double unitPrice = orderDetail.getSparePart().getPrice();
        dto.setPrice(unitPrice * orderDetail.getQuantity());
        return dto;
    }
}