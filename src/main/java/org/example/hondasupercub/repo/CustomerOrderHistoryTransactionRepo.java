package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerOrderHistoryTransactionRepo extends JpaRepository<Transaction, Integer> {
}