package org.example.hondasupercub.controller;


import com.itextpdf.text.DocumentException;
import org.example.hondasupercub.dto.OrderDTO;
import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.service.impl.AdminOrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/adminorders")
public class AdminOrderController {
    @Autowired
    private AdminOrderServiceImpl adminOrderService;

    // ✅ Get all orders
    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAllOrders() {
        List<OrderDTO> orders = adminOrderService.getAllOrders();
        ResponseDTO responseDTO = new ResponseDTO(200, "Orders retrieved successfully", orders);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // ✅ Get order by ID
    @GetMapping("/get/{orderId}")
    public ResponseEntity<ResponseDTO> getOrderById(@PathVariable int orderId) {
        OrderDTO order = adminOrderService.getOrderById(orderId);
        if (order != null) {
            ResponseDTO responseDTO = new ResponseDTO(200, "Order retrieved successfully", order);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            ResponseDTO responseDTO = new ResponseDTO(404, "Order not found", null);
            return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
        }
    }

    // ✅ Update order status
    @PutMapping("/update/{orderId}")
    public ResponseEntity<ResponseDTO> updateOrderStatus(@PathVariable int orderId, @RequestBody OrderDTO orderDTO) {
        OrderDTO updatedOrder = adminOrderService.updateOrderStatus(orderId, orderDTO.getOrderStatus());
        if (updatedOrder != null) {
            ResponseDTO responseDTO = new ResponseDTO(200, "Order status updated successfully", updatedOrder);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            ResponseDTO responseDTO = new ResponseDTO(404, "Order not found", null);
            return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
        }
    }

    // ✅ Cancel order
    @DeleteMapping("/cancel/{orderId}")
    public ResponseEntity<ResponseDTO> cancelOrder(@PathVariable int orderId) {
        boolean cancelled = adminOrderService.cancelOrder(orderId);
        if (cancelled) {
            ResponseDTO responseDTO = new ResponseDTO(200, "Order cancelled successfully", null);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            ResponseDTO responseDTO = new ResponseDTO(404, "Order not found", null);
            return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
        }
    }

    // ✅ Filter orders by status
    @GetMapping("/filter")
    public ResponseEntity<ResponseDTO> filterOrdersByStatus(@RequestParam String status) {
        List<OrderDTO> filteredOrders = adminOrderService.filterOrdersByStatus(status);
        ResponseDTO responseDTO = new ResponseDTO(200, "Orders filtered successfully", filteredOrders);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }


    // ✅ Search orders by customer name
    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchOrdersByCustomerName(@RequestParam String query) {
        List<OrderDTO> searchedOrders = adminOrderService.searchOrdersByCustomerName(query);
        ResponseDTO responseDTO = new ResponseDTO(200, "Orders searched successfully", searchedOrders);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/report/download")
    public ResponseEntity<InputStreamResource> downloadOrderReport() throws IOException, DocumentException {
        ByteArrayInputStream pdfReport = adminOrderService.generateOrderPdfReport();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=order_report.pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(pdfReport));
    }
}
