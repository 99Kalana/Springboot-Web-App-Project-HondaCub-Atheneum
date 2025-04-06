package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerCheckoutOrderRepo extends JpaRepository<Order, Integer> {
}
