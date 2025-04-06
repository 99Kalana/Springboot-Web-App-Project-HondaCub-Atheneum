package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerProductsSellerRepo extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.role = 'SELLER'")
    List<User> findSellers();
}


