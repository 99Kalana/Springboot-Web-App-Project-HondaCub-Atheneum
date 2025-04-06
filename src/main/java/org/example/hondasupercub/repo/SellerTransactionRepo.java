package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerTransactionRepo extends JpaRepository<Transaction, Integer> {

    @Query("SELECT t FROM Transaction t JOIN t.order o JOIN o.orderDetails od JOIN od.sparePart sp JOIN sp.seller s WHERE s.userId = :sellerId")
    List<Transaction> findTransactionsBySellerId(@Param("sellerId") int sellerId);

    @Query("SELECT t FROM Transaction t JOIN t.order o JOIN o.orderDetails od JOIN od.sparePart sp JOIN sp.seller s WHERE t.transactionId = :transactionId AND s.userId = :sellerId")
    Optional<Transaction> findTransactionByIdAndSellerId(@Param("transactionId") int transactionId, @Param("sellerId") int sellerId);




    @Query("SELECT SUM(t.paidAmount) FROM Transaction t JOIN t.order o JOIN o.orderDetails od JOIN od.sparePart sp WHERE sp.seller.userId = :sellerId AND t.paymentStatus = 'COMPLETED'")
    Double calculateTotalSalesBySellerId(@Param("sellerId") int sellerId);

}