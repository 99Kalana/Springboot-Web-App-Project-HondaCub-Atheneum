package org.example.hondasupercub.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TransactionDTO {

    private int transactionId;

    private int orderId;   // Store only the orderId, not the full Order entity

    private int userId;    // Store only the userId, not the full User entity

    private String paymentMethod;

    private String paymentStatus;

    private String refundStatus;

    private double paidAmount;

    private String transactionDate;

    private String shippingAddress;

    private String contactNumber;


    private String authorizationHeader; //Important to get user data


}
