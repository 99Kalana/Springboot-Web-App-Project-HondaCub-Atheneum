package org.example.hondasupercub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Shipping address cannot be blank")
    @Size(max = 255, message = "Shipping address cannot exceed 255 characters")
    private String shippingAddress;

    @NotBlank(message = "Contact number cannot be blank")
    @Pattern(regexp = "^\\d{10}$", message = "Contact number must be a 10-digit number")
    private String contactNumber;


    private String authorizationHeader; //Important to get user data

    private int redeemedPoints; // Add this field


}
