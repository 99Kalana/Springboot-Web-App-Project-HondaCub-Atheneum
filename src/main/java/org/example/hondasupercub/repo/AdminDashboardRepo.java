package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminDashboardRepo {
    long countUsers();
    long countOrders();
    long countSpareParts();
    @Query("SELECT SUM(t.paidAmount) FROM Transaction t")
    double sumTransactions();
}