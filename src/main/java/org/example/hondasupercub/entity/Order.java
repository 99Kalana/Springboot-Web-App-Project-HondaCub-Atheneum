package org.example.hondasupercub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    private String placedAt;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;

    public enum OrderStatus {
        PENDING, SHIPPED, DELIVERED, CANCELLED, RETURNED
    }
}
