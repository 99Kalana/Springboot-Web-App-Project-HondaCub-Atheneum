package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Transaction;
import org.example.hondasupercub.entity.Transaction.PaymentStatus;
import org.example.hondasupercub.entity.Transaction.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminTransactionRepo extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByOrder_OrderId(int orderId);
    List<Transaction> findByPaymentStatus(PaymentStatus paymentStatus);
    List<Transaction> findByRefundStatus(RefundStatus refundStatus);

    @Query("SELECT COALESCE(SUM(t.paidAmount), 0) FROM Transaction t")
    double sumTransactions();

}