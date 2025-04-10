// AdminOrderDetailRepo.java
package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminOrderDetailRepo extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrder_OrderId(int orderId);
}