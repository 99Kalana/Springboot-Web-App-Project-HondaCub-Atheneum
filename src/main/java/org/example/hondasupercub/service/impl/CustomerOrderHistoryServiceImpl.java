package org.example.hondasupercub.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.hondasupercub.dto.OrderDetailDTO;
import org.example.hondasupercub.dto.OrderDTO;
import org.example.hondasupercub.entity.Order;
import org.example.hondasupercub.entity.OrderDetail;
import org.example.hondasupercub.entity.User;
import org.example.hondasupercub.repo.*;
import org.example.hondasupercub.service.CustomerOrderHistoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerOrderHistoryServiceImpl implements CustomerOrderHistoryService {

    @Autowired
    private CustomerOrderHistoryRepo orderRepo;

    @Autowired
    private CustomerOrderHistoryUserRepo userRepo;

    @Autowired
    private CustomerOrderHistoryOrderDetailRepo orderDetailRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public List<OrderDTO> getOrderHistory(String authorizationHeader, Integer orderId, String status) {
        String email = extractEmailFromToken(authorizationHeader);
        if (email == null) {
            return List.of();
        }

        User user = userRepo.findByEmail(email);
        if (user == null) {
            return List.of();
        }

        List<Order> orders = orderRepo.findByUser(user);

        // Apply filters
        if (orderId != null) {
            orders = orders.stream()
                    .filter(order -> order.getOrderId() == orderId)
                    .collect(Collectors.toList());
        }

        if (status != null && !status.isEmpty()) {
            try {
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status);
                orders = orders.stream()
                        .filter(order -> order.getOrderStatus() == orderStatus)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Handle invalid status
                return Collections.emptyList();
            }
        }

        return orders.stream()
                .map(this::mapOrderToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> searchOrderHistory(int orderId, String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        if (email == null) {
            throw new RuntimeException("Invalid or missing authentication token");
        }

        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Order order = orderRepo.findById(orderId).orElse(null);
        if (order == null || order.getUser().getUserId() != user.getUserId()) {
            return Collections.emptyList(); // Return empty list if order not found or doesn't belong to the user
        }

        return Collections.singletonList(mapOrderToDTO(order)); // Return the order as a list
    }

    // Helper method to map Order to OrderDTO
    private OrderDTO mapOrderToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUser().getUserId());
        dto.setOrderStatus(order.getOrderStatus().toString());
        dto.setPlacedAt(order.getPlacedAt().toString());
        return dto;
    }

    @Override
    public List<OrderDetailDTO> getOrderDetails(int orderId, String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        if (email == null) {
            return List.of();
        }

        Order order = orderRepo.findById(orderId).orElse(null);
        if (order == null) {
            return List.of();
        }

        List<OrderDetail> orderDetails = orderDetailRepo.findByOrder(order);
        return orderDetails.stream()
                .map(orderDetail -> {
                    OrderDetailDTO dto = new OrderDetailDTO();
                    dto.setOrderDetailId(orderDetail.getOrderDetailId());
                    dto.setOrderId(orderDetail.getOrder().getOrderId());
                    dto.setSparePartId(orderDetail.getSparePart().getPartId());
                    dto.setQuantity(orderDetail.getQuantity());
                    dto.setPrice(orderDetail.getPrice());
                    dto.setSparePartName(orderDetail.getSparePart().getPartName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private String extractEmailFromToken(String authorizationHeader) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                Claims claims = Jwts.parser().setSigningKey(secretKey)
                        .parseClaimsJws(authorizationHeader.substring(7)).getBody();
                return claims.getSubject();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }



    @Override
    public byte[] generateBill(int orderId, String authorizationHeader) throws Exception {
        String email = extractEmailFromToken(authorizationHeader);
        if (email == null) {
            throw new RuntimeException("Invalid or missing authentication token");
        }

        Order order = orderRepo.findById(orderId).orElse(null);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        List<OrderDetail> orderDetails = orderDetailRepo.findByOrder(order);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

        // Header
        Paragraph header = new Paragraph("HondaCub Atheneum", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLUE));
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);
        document.add(Chunk.NEWLINE);

        // Order Details
        document.add(new Paragraph("Order Bill", titleFont));
        document.add(new Paragraph("Order ID: " + order.getOrderId(), regularFont));
        document.add(new Paragraph("Order Date: " + order.getPlacedAt(), regularFont));
        document.add(new Paragraph("Order Status: " + order.getOrderStatus(), regularFont));
        document.add(Chunk.NEWLINE);

        // Items Table
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        addTableHeader(table, boldFont);

        for (OrderDetail detail : orderDetails) {
            addTableRow(table, detail, regularFont);
        }

        document.add(table);

        // Total Price
        double totalPrice = orderDetails.stream()
                .mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
                .sum();

        document.add(new Paragraph("Total Price: $" + totalPrice, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));

        // Footer
        Paragraph footer = new Paragraph("Thank you for your purchase!", FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20f);
        document.add(footer);

        document.close();

        return outputStream.toByteArray();
    }

    private void addTableHeader(PdfPTable table, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase("Item", font));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Quantity", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Price", font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
    }

    private void addTableRow(PdfPTable table, OrderDetail detail, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(detail.getSparePart().getPartName(), font));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(String.valueOf(detail.getQuantity()), font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("$" + detail.getPrice(), font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
    }


}