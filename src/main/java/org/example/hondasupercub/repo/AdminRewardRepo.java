package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Reward;
import org.example.hondasupercub.entity.Reward.RewardLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRewardRepo extends JpaRepository<Reward, Integer> {
    List<Reward> findByUser_FullNameContainingIgnoreCase(String fullName);
    List<Reward> findByRewardLevel(RewardLevel rewardLevel);
}