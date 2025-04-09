package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRewardRepo extends JpaRepository<Reward, Integer> {
    Optional<Reward> findByUser_UserId(int userId);
}