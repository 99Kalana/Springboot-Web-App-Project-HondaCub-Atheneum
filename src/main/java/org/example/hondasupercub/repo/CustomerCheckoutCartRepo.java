package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Cart;
import org.example.hondasupercub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerCheckoutCartRepo extends JpaRepository<Cart, Integer> {
    List<Cart> findByUser(User user);

    void deleteByUser(User user);
}
