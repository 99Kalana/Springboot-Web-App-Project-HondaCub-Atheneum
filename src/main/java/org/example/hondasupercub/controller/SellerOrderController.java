package org.example.hondasupercub.controller;

import org.example.hondasupercub.dto.OrderDTO;
import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.entity.Order;
import org.example.hondasupercub.service.SellerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/seller/orders")
public class SellerOrderController {

    @Autowired
    private SellerOrderService sellerOrderService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getOrdersBySeller(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(value = "orderId", required = false) Integer orderId,
            @RequestParam(value = "status", required = false) String status) {

        List<OrderDTO> orders;
        if (orderId != null) {
            // Search by orderId
            Optional<OrderDTO> orderDTO = sellerOrderService.getOrderByOrderIdAndSellerId(orderId, authorizationHeader);
            if (orderDTO.isPresent()) {
                orders = List.of(orderDTO.get()); // Wrap in a list
            } else {
                return new ResponseEntity<>(new ResponseDTO(HttpStatus.NOT_FOUND.value(), "Order not found", null), HttpStatus.NOT_FOUND);
            }
        } else if (status != null && !status.isEmpty()) {
            // Filter by status
            orders = sellerOrderService.getOrdersBySellerIdAndStatus(authorizationHeader, status);
            if (orders.isEmpty()) {
                return new ResponseEntity<>(new ResponseDTO(HttpStatus.NOT_FOUND.value(), "No orders found with the specified status", null), HttpStatus.NOT_FOUND);
            }
        } else {
            // Fetch all orders
            orders = sellerOrderService.getOrdersBySellerId(authorizationHeader);
        }
        return new ResponseEntity<>(new ResponseDTO(HttpStatus.OK.value(), "Orders fetched successfully", orders), HttpStatus.OK);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<ResponseDTO> updateOrderStatus(@PathVariable int orderId, @RequestParam("status") Order.OrderStatus status, @RequestHeader("Authorization") String authorizationHeader) {
        OrderDTO updatedOrder = sellerOrderService.updateOrderStatus(orderId, status, authorizationHeader);
        if (updatedOrder != null) {
            return new ResponseEntity<>(new ResponseDTO(HttpStatus.OK.value(), "Order status updated successfully", updatedOrder), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseDTO(HttpStatus.NOT_FOUND.value(), "Order not found or not belonging to seller", null), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{orderId}/details")
    public ResponseEntity<ResponseDTO> getOrderDetails(@PathVariable int orderId, @RequestHeader("Authorization") String authorizationHeader) {
        Optional<OrderDTO> orderDTO = sellerOrderService.getOrderByOrderIdAndSellerId(orderId, authorizationHeader);
        if (orderDTO.isPresent()) {
            return new ResponseEntity<>(new ResponseDTO(HttpStatus.OK.value(), "Order details fetched successfully", orderDTO.get()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseDTO(HttpStatus.NOT_FOUND.value(), "Order not found or not belonging to seller", null), HttpStatus.NOT_FOUND);
        }
    }
}