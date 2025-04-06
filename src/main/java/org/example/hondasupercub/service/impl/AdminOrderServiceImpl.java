package org.example.hondasupercub.service.impl;

import org.example.hondasupercub.dto.OrderDTO;
import org.example.hondasupercub.entity.Order;
import org.example.hondasupercub.repo.AdminOrderRepo;
import org.example.hondasupercub.service.AdminOrderService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdminOrderServiceImpl implements AdminOrderService {
    @Autowired
    private AdminOrderRepo adminOrderRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = adminOrderRepo.findAll();
        return orders.stream().map(this::convertToDTO).toList(); // Use convertToDTO
    }

    @Override
    public OrderDTO getOrderById(int orderId) {
        Optional<Order> order = adminOrderRepo.findById(orderId);
        return order.map(this::convertToDTO).orElse(null); // Use convertToDTO
    }

    @Override
    public OrderDTO updateOrderStatus(int orderId, String status) {
        Optional<Order> orderOptional = adminOrderRepo.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setOrderStatus(Order.OrderStatus.valueOf(status.toUpperCase()));
            return convertToDTO(adminOrderRepo.save(order)); // Use convertToDTO
        } else {
            return null;
        }
    }

    @Override
    public boolean cancelOrder(int orderId) {
        Optional<Order> orderOptional = adminOrderRepo.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setOrderStatus(Order.OrderStatus.CANCELLED);
            adminOrderRepo.save(order);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<OrderDTO> filterOrdersByStatus(String status) {
        if ("all".equalsIgnoreCase(status)) {
            return getAllOrders();
        }
        List<Order> orders = adminOrderRepo.findByOrderStatus(Order.OrderStatus.valueOf(status.toUpperCase()));
        return orders.stream().map(this::convertToDTO).toList(); // Use convertToDTO
    }

    @Override
    public List<OrderDTO> searchOrdersByCustomerName(String query) {
        List<Order> orders = adminOrderRepo.findByUser_FullNameContainingIgnoreCase(query);
        return orders.stream().map(this::convertToDTO).toList(); // Use convertToDTO
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = modelMapper.map(order, OrderDTO.class);
        if (order.getUser() != null) {
            System.out.println("User Full Name: " + order.getUser().getFullName()); // Debugging line
            dto.setFullName(order.getUser().getFullName());
        } else {
            System.out.println("User is null for order ID: " + order.getOrderId()); // Debugging line
        }
        return dto;
    }

}
