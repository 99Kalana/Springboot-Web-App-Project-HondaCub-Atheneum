package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminOrderRepo extends JpaRepository<Order, Integer> {
    List<Order> findByOrderStatus(Order.OrderStatus orderStatus);
    List<Order> findByUser_FullNameContainingIgnoreCase(String fullName);
}
