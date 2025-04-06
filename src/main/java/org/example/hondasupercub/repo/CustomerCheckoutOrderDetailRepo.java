package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerCheckoutOrderDetailRepo extends JpaRepository<OrderDetail, Integer> {
}
