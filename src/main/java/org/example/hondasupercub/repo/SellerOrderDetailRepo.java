package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerOrderDetailRepo extends JpaRepository<OrderDetail, Integer> {
}