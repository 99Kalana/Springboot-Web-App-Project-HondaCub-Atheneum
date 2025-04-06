package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Order;
import org.example.hondasupercub.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerOrderHistoryOrderDetailRepo extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrder(Order order);
}