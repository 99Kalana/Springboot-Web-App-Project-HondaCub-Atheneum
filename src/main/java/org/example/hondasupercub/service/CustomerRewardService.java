package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.RewardDTO;

public interface CustomerRewardService {
    RewardDTO getCustomerRewardDetails(String authorizationHeader);
}