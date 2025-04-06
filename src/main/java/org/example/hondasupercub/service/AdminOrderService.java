package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.OrderDTO;

import java.util.List;

public interface AdminOrderService {
    List<OrderDTO> getAllOrders();
    OrderDTO getOrderById(int orderId);
    OrderDTO updateOrderStatus(int orderId, String status);
    boolean cancelOrder(int orderId);
    List<OrderDTO> filterOrdersByStatus(String status);
    List<OrderDTO> searchOrdersByCustomerName(String query);
}
