package org.example.hondasupercub.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.hondasupercub.dto.OrderDTO;
import org.example.hondasupercub.entity.Order;
import org.example.hondasupercub.entity.OrderDetail;
import org.example.hondasupercub.repo.AdminOrderDetailRepo;
import org.example.hondasupercub.repo.AdminOrderRepo;
import org.example.hondasupercub.service.AdminOrderService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public class AdminOrderServiceImpl implements AdminOrderService {
    @Autowired
    private AdminOrderRepo adminOrderRepo;

    @Autowired
    private AdminOrderDetailRepo adminOrderDetailRepo;

    @Autowired
    private ModelMapper modelMapper;

    private static final Font MAIN_TOPIC_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
    private static final Font SUB_TOPIC_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
    private static final Font TABLE_HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font CELL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9);
    private static final BaseColor TABLE_HEADER_BG_COLOR = BaseColor.LIGHT_GRAY;
    private static final Font DATE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8);
    private static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8);


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
            dto.setFullName(order.getUser().getFullName());
            dto.setUserId(order.getUser().getUserId());
        }

        // Calculate total amount
        List<OrderDetail> orderDetails = adminOrderDetailRepo.findByOrder_OrderId(order.getOrderId()); // Updated repository name here
        double totalAmount = 0;
        for (OrderDetail detail : orderDetails) {
            totalAmount += detail.getQuantity() * detail.getPrice();
        }
        dto.setTotalAmount(totalAmount);

        dto.setOrderDetailIds(order.getOrderDetails().stream()
                .map(OrderDetail::getOrderDetailId)
                .collect(Collectors.toList()));

        return dto;
    }

    public ByteArrayInputStream generateOrderPdfReport() throws DocumentException, IOException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();

        // Main Topic
        Paragraph mainTopic = new Paragraph("Honda Cub Atheneum", MAIN_TOPIC_FONT);
        mainTopic.setAlignment(Element.ALIGN_CENTER);
        document.add(mainTopic);

        // Sub Topic
        Paragraph subTopic = new Paragraph("Admin Order Management Report", SUB_TOPIC_FONT);
        subTopic.setAlignment(Element.ALIGN_CENTER);
        document.add(subTopic);

        document.add(Chunk.NEWLINE);

        // Date and Time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Paragraph dateTime = new Paragraph("Generated on: " + dtf.format(LocalDateTime.now()), DATE_FONT);
        dateTime.setAlignment(Element.ALIGN_LEFT);
        document.add(dateTime);

        document.add(Chunk.NEWLINE);

        // Fetch all orders
        List<Order> orders = adminOrderRepo.findAll();

        // Create PDF table
        PdfPTable table = new PdfPTable(6); // Adjust column count
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Add table headers
        Stream.of("Order ID", "Customer Name", "Order Date", "Status", "Total Amount", "Item Details")
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
            if (order.getUser() != null) {
                table.addCell(new Phrase(order.getUser().getFullName(), CELL_FONT));
            } else {
                table.addCell(new Phrase("N/A", CELL_FONT));
            }
            table.addCell(new Phrase(order.getPlacedAt(), CELL_FONT));
            table.addCell(new Phrase(order.getOrderStatus().toString(), CELL_FONT));

            // Calculate total amount for the row
            List<OrderDetail> orderDetails = adminOrderDetailRepo.findByOrder_OrderId(order.getOrderId());
            double totalAmount = 0;
            StringBuilder itemDetails = new StringBuilder();
            for (OrderDetail detail : orderDetails) {
                totalAmount += detail.getQuantity() * detail.getPrice();
                itemDetails.append(detail.getSparePart().getPartName())
                        .append(" (Qty: ").append(detail.getQuantity())
                        .append(", Price: ").append(detail.getPrice()).append(")\n");
            }
            table.addCell(new Phrase(String.valueOf(totalAmount), CELL_FONT));
            table.addCell(new Phrase(itemDetails.toString(), CELL_FONT));
        }

        document.add(table);

        // Footer
        Paragraph footer = new Paragraph("End of Order Report", FOOTER_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20f);
        document.add(footer);

        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

}
