package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerOrderRepo extends JpaRepository<Order, Integer> {

    @Query("SELECT o FROM Order o JOIN o.orderDetails od JOIN od.sparePart sp WHERE sp.seller.userId = :sellerId")
    List<Order> findOrdersBySellerId(@Param("sellerId") int sellerId);

    @Query("SELECT o FROM Order o JOIN FETCH o.orderDetails WHERE o.orderId = :orderId")
    Optional<Order> findByIdWithOrderDetails(@Param("orderId") int orderId);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderDetails od LEFT JOIN FETCH od.sparePart sp LEFT JOIN FETCH sp.seller s WHERE o.orderId = :orderId AND s.userId = :sellerId")
    Optional<Order> findOrderByOrderIdAndSellerId(@Param("orderId") int orderId, @Param("sellerId") int sellerId);



    @Query("SELECT COUNT(o) FROM Order o JOIN o.orderDetails od JOIN od.sparePart sp WHERE sp.seller.userId = :sellerId AND o.orderStatus = 'PENDING'")
    long countPendingOrdersBySellerId(@Param("sellerId") int sellerId);


}