package org.example.hondasupercub.controller;

import org.example.hondasupercub.dto.OrderDTO;
import org.example.hondasupercub.dto.OrderDetailDTO;
import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.service.CustomerOrderHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer/orders")
public class CustomerOrderHistoryController {

    @Autowired
    private CustomerOrderHistoryService orderHistoryService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getOrderHistory(
            @RequestParam(value = "orderId", required = false) Integer orderId,
            @RequestParam(value = "status", required = false) String status,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            List<OrderDTO> orderHistory = orderHistoryService.getOrderHistory(authorizationHeader, orderId, status);
            ResponseDTO responseDTO = new ResponseDTO(200, "Order history retrieved successfully.", orderHistory);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            ResponseDTO responseDTO = new ResponseDTO(500, "Failed to retrieve order history: " + e.getMessage(), null);
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{orderId}/details")
    public ResponseEntity<ResponseDTO> getOrderDetails(@PathVariable int orderId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            List<OrderDetailDTO> orderDetails = orderHistoryService.getOrderDetails(orderId, authorizationHeader);
            ResponseDTO responseDTO = new ResponseDTO(200, "Order details retrieved successfully.", orderDetails);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            ResponseDTO responseDTO = new ResponseDTO(500, "Failed to retrieve order details: " + e.getMessage(), null);
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchOrderHistory(
            @RequestParam("orderId") int orderId,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            List<OrderDTO> orderHistory = orderHistoryService.searchOrderHistory(orderId, authorizationHeader);
            ResponseDTO responseDTO = new ResponseDTO(200, "Order history retrieved successfully.", orderHistory);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            ResponseDTO responseDTO = new ResponseDTO(500, "Failed to retrieve order history: " + e.getMessage(), null);
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping("/{orderId}/bill")
    public ResponseEntity<byte[]> generateBill(@PathVariable int orderId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            byte[] pdfBytes = orderHistoryService.generateBill(orderId, authorizationHeader);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "attachment; filename=order-bill-" + orderId + ".pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}