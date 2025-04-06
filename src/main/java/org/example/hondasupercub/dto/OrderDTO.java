package org.example.hondasupercub.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OrderDTO {

    private int orderId;

    private int userId;  // Store only the userId, not the full User entity

    private String fullName;

    private String orderStatus;  // Store as a String (e.g., "PENDING", "SHIPPED")

    private String placedAt;

    private List<Integer> orderDetailIds;  // Store only the IDs of related order details
}
