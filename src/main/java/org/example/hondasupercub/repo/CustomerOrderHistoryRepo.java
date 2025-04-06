package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Order;
import org.example.hondasupercub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerOrderHistoryRepo extends JpaRepository<Order, Integer> {
    List<Order> findByUser(User user);
}