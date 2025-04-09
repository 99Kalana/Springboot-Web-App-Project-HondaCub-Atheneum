package org.example.hondasupercub.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Data
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transactionId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private RefundStatus refundStatus = RefundStatus.NONE;

    private double paidAmount;

    private String transactionDate;

    private String shippingAddress;

    private String contactNumber;

    private int redeemedPoints = 0;

    public enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, PAYPAL
    }

    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED
    }

    public enum RefundStatus {
        NONE, PARTIAL, FULL
    }
}
