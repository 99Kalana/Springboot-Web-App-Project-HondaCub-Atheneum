package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerOrderHistoryUserRepo extends JpaRepository<User, Integer> {
    User findByEmail(String email);
}