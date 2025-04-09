package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.OrderDTO;
import org.example.hondasupercub.dto.OrderDetailDTO;
import org.example.hondasupercub.entity.Order.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface SellerOrderService {

    List<OrderDTO> getOrdersBySellerId(String authorizationHeader);

    List<OrderDTO> getOrdersBySellerIdAndStatus(String authorizationHeader, String status); // New method

    OrderDTO updateOrderStatus(int orderId, OrderStatus newStatus, String authorizationHeader);

    List<OrderDetailDTO> getOrderDetailsByOrderId(int orderId, String authorizationHeader);

    Optional<OrderDTO> getOrderByOrderIdAndSellerId(int orderId, String authorizationHeader);
}