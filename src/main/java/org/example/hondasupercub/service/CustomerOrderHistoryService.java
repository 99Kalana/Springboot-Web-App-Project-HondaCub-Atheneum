package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.OrderDetailDTO;
import org.example.hondasupercub.dto.OrderDTO;

import java.util.List;

public interface CustomerOrderHistoryService {
    List<OrderDTO> getOrderHistory(String authorizationHeader, Integer orderId, String status);
    List<OrderDetailDTO> getOrderDetails(int orderId, String authorizationHeader);
    List<OrderDTO> searchOrderHistory(int orderId, String authorizationHeader);

    byte[] generateBill(int orderId, String authorizationHeader) throws Exception;
}