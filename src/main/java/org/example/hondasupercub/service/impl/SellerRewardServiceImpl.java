package org.example.hondasupercub.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.hondasupercub.dto.RewardDTO;
import org.example.hondasupercub.entity.Reward;
import org.example.hondasupercub.entity.User;
import org.example.hondasupercub.repo.SellerRewardRepo;
import org.example.hondasupercub.repo.SparePartRepo;
import org.example.hondasupercub.repo.UserRepository;
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
    private UserRepository userRepo;

    @Autowired
    private SparePartRepo sparePartRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public List<RewardDTO> getRewardsBySeller(String authorizationHeader) {
        int sellerId = extractSellerIdFromToken(authorizationHeader);
        Optional<Reward> existingReward = rewardRepo.findRewardsBySellerId(sellerId).stream().findFirst();
        long sparePartCount = sparePartRepo.countBySellerUserId(sellerId);
        RewardDTO calculatedRewardDTO = calculateReward(sparePartCount, sellerId);
        Reward calculatedRewardEntity = modelMapper.map(calculatedRewardDTO, Reward.class);

        if (existingReward.isEmpty()) {
            rewardRepo.save(calculatedRewardEntity);
            return List.of(calculatedRewardDTO);
        } else {
            Reward rewardToUpdate = existingReward.get();
            if (!rewardToUpdate.getRewardLevel().equals(mapToEntityRewardLevel(calculatedRewardDTO.getRewardLevel())) || rewardToUpdate.getPoints() != calculatedRewardDTO.getPoints()) {
                rewardToUpdate.setRewardLevel(mapToEntityRewardLevel(calculatedRewardDTO.getRewardLevel()));
                rewardToUpdate.setPoints(calculatedRewardDTO.getPoints());
                rewardToUpdate.setLastUpdated(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                rewardRepo.save(rewardToUpdate);
            }
            return List.of(modelMapper.map(rewardToUpdate, RewardDTO.class));
        }
    }

    // Helper method to map DTO RewardLevel to Entity RewardLevel
    private Reward.RewardLevel mapToEntityRewardLevel(RewardDTO.RewardLevel dtoLevel) {
        return Reward.RewardLevel.valueOf(dtoLevel.name());
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
                return null; // Insufficient points
            }
        } else {
            return null; // Reward not found or not belonging to seller
        }
    }

    @Override
    public RewardDTO calculateReward(long sparePartCount, int sellerId) {
        Reward reward = new Reward();
        Optional<User> sellerOptional = userRepo.findById(sellerId);
        sellerOptional.ifPresent(reward::setUser);
        reward.setLastUpdated(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        if (sparePartCount >= 30) {
            reward.setRewardLevel(Reward.RewardLevel.PLATINUM);
            reward.setPoints(500);
        } else if (sparePartCount >= 20) {
            reward.setRewardLevel(Reward.RewardLevel.GOLD);
            reward.setPoints(300);
        } else if (sparePartCount >= 10) {
            reward.setRewardLevel(Reward.RewardLevel.SILVER);
            reward.setPoints(200);
        } else if (sparePartCount >= 5) {
            reward.setRewardLevel(Reward.RewardLevel.BRONZE);
            reward.setPoints(100);
        } else {
            reward.setRewardLevel(Reward.RewardLevel.BRONZE); // Default level
            reward.setPoints(0);
        }
        return modelMapper.map(reward, RewardDTO.class); // Map the entity to DTO before returning
    }

    private int extractSellerIdFromToken(String authorizationHeader) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authorizationHeader.substring(7)).getBody();
        return (Integer) claims.get("userId");
    }
}