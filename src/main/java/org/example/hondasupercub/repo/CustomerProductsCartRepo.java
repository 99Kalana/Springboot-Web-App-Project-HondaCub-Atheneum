package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerProductsCartRepo extends JpaRepository<Cart, Integer> {
    List<Cart> findByUser_UserId(int userId);
}