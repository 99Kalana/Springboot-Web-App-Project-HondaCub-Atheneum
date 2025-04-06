package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.RewardDTO;

import java.util.List;

public interface AdminRewardService {
    List<RewardDTO> getAllRewards();
    RewardDTO getRewardById(int rewardId);
    List<RewardDTO> searchRewards(String query);
    List<RewardDTO> filterRewardsByLevel(String level);
    RewardDTO updateReward(int rewardId, int points);
}