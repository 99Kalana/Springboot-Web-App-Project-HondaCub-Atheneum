package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.RewardDTO;

import java.util.List;

public interface SellerRewardService {

    List<RewardDTO> getRewardsBySeller(String authorizationHeader);

    RewardDTO getRewardDetails(int rewardId, String authorizationHeader);

    RewardDTO redeemPoints(int rewardId, int pointsToRedeem, String authorizationHeader);
}