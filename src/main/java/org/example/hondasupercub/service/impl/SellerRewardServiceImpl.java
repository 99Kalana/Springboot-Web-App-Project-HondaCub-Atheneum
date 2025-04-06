package org.example.hondasupercub.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.hondasupercub.dto.RewardDTO;
import org.example.hondasupercub.entity.Reward;
import org.example.hondasupercub.repo.SellerRewardRepo;
import org.example.hondasupercub.service.SellerRewardService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SellerRewardServiceImpl implements SellerRewardService {

    @Autowired
    private SellerRewardRepo rewardRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public List<RewardDTO> getRewardsBySeller(String authorizationHeader) {
        int sellerId = extractSellerIdFromToken(authorizationHeader);
        List<Reward> rewards = rewardRepo.findRewardsBySellerId(sellerId);
        return rewards.stream()
                .map(reward -> modelMapper.map(reward, RewardDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public RewardDTO getRewardDetails(int rewardId, String authorizationHeader) {
        int sellerId = extractSellerIdFromToken(authorizationHeader);
        Optional<Reward> reward = rewardRepo.findRewardByIdAndSellerId(rewardId, sellerId);
        return reward.map(value -> modelMapper.map(value, RewardDTO.class)).orElse(null);
    }

    @Override
    public RewardDTO redeemPoints(int rewardId, int pointsToRedeem, String authorizationHeader) {
        int sellerId = extractSellerIdFromToken(authorizationHeader);
        Optional<Reward> rewardOptional = rewardRepo.findRewardByIdAndSellerId(rewardId, sellerId);

        if (rewardOptional.isPresent()) {
            Reward reward = rewardOptional.get();
            if (reward.getPoints() >= pointsToRedeem) {
                reward.setPoints(reward.getPoints() - pointsToRedeem);
                reward.setRedeemedPoints(reward.getRedeemedPoints() + pointsToRedeem);
                reward.setLastUpdated(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                rewardRepo.save(reward);
                return modelMapper.map(reward, RewardDTO.class);
            } else {
                // Handle insufficient points scenario
                return null;
            }
        } else {
            // Handle reward not found
            return null;
        }
    }

    private int extractSellerIdFromToken(String authorizationHeader) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authorizationHeader.substring(7)).getBody();
        return (Integer) claims.get("userId");
    }
}