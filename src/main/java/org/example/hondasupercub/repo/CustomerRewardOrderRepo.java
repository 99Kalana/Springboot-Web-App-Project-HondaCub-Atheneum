package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRewardOrderRepo extends JpaRepository<Order, Integer> {
    List<Order> findByUser_UserIdAndOrderStatus(int userId, Order.OrderStatus orderStatus);
}