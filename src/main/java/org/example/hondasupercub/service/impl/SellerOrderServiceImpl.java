package org.example.hondasupercub.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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

    private static final Font MAIN_TOPIC_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
    private static final Font SUB_TOPIC_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
    private static final Font TABLE_HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font CELL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9);
    private static final BaseColor TABLE_HEADER_BG_COLOR = BaseColor.LIGHT_GRAY;
    private static final Font DATE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8);
    private static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8);


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

    public ByteArrayInputStream generateSellerOrderPdfReport(String authorizationHeader) throws DocumentException, IOException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();

        // Extract Seller ID from Token
        int sellerId = extractSellerIdFromToken(authorizationHeader);

        // Main Topic
        Paragraph mainTopic = new Paragraph("Honda Cub Atheneum - Seller Report", MAIN_TOPIC_FONT);
        mainTopic.setAlignment(Element.ALIGN_CENTER);
        document.add(mainTopic);

        // Sub Topic
        Paragraph subTopic = new Paragraph("Seller Order Management (Seller ID: " + sellerId + ")", SUB_TOPIC_FONT);
        subTopic.setAlignment(Element.ALIGN_CENTER);
        document.add(subTopic);

        document.add(Chunk.NEWLINE);

        // Date and Time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Paragraph dateTime = new Paragraph("Generated on: " + dtf.format(LocalDateTime.now()), DATE_FONT);
        dateTime.setAlignment(Element.ALIGN_LEFT);
        document.add(dateTime);

        document.add(Chunk.NEWLINE);

        // Fetch seller's orders
        List<Order> orders = orderRepo.findOrdersBySellerId(sellerId);

        // Create PDF table
        PdfPTable table = new PdfPTable(4); // Adjust column count based on displayed data
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Add table headers
        Stream.of("Order ID", "User ID", "Order Status", "Placed At")
                .forEach(columnTitle -> {
                    PdfPCell headerCell = new PdfPCell();
                    headerCell.setBackgroundColor(TABLE_HEADER_BG_COLOR);
                    headerCell.setBorderWidth(1);
                    headerCell.setPhrase(new Phrase(columnTitle, TABLE_HEADER_FONT));
                    table.addCell(headerCell);
                });

        // Add table data
        for (Order order : orders) {
            table.addCell(new Phrase(String.valueOf(order.getOrderId()), CELL_FONT));
            table.addCell(new Phrase(String.valueOf(order.getUser().getUserId()), CELL_FONT));
            table.addCell(new Phrase(order.getOrderStatus().toString(), CELL_FONT));
            table.addCell(new Phrase(order.getPlacedAt().toString(), CELL_FONT));
        }

        document.add(table);

        // Footer
        Paragraph footer = new Paragraph("End of Seller Orders Report", FOOTER_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20f);
        document.add(footer);

        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

}