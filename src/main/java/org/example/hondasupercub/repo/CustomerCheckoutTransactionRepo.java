package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerCheckoutTransactionRepo extends JpaRepository<Transaction, Integer> {
}
