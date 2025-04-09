package org.example.hondasupercub.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.hondasupercub.dto.RewardDTO;
import org.example.hondasupercub.entity.Order;
import org.example.hondasupercub.entity.Reward;
import org.example.hondasupercub.entity.User;
import org.example.hondasupercub.repo.CustomerRewardOrderRepo;
import org.example.hondasupercub.repo.CustomerRewardRepo;
import org.example.hondasupercub.repo.CustomerRewardUserRepo;
import org.example.hondasupercub.service.CustomerRewardService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CustomerRewardServiceImpl implements CustomerRewardService {

    @Autowired
    private CustomerRewardRepo rewardRepo;

    @Autowired
    private CustomerRewardUserRepo userRepo;

    @Autowired
    private CustomerRewardOrderRepo orderRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${jwt.secret}")
    private String secretKey;

    private int extractCustomerIdFromToken(String authorizationHeader) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authorizationHeader.substring(7)).getBody();
        return (Integer) claims.get("userId");
    }

    @Override
    public RewardDTO getCustomerRewardDetails(String authorizationHeader) {
        int customerId = extractCustomerIdFromToken(authorizationHeader);
        User customer = userRepo.findById(customerId).orElse(null);

        if (customer == null || customer.getRole() != User.UserRole.CUSTOMER) {
            return null;
        }

        List<Order> deliveredOrders = orderRepo.findByUser_UserIdAndOrderStatus(customerId, Order.OrderStatus.DELIVERED);
        int deliveredOrderCount = deliveredOrders.size();

        Reward customerReward = rewardRepo.findByUser_UserId(customerId).orElseGet(() -> {
            Reward newReward = new Reward();
            newReward.setUser(customer);
            return newReward;
        });

        Reward.RewardLevel rewardLevel = customerReward.getRewardLevel();
        int earnedPoints = 0; // Initialize earnedPoints to 0

        if (deliveredOrderCount >= 50) {
            rewardLevel = Reward.RewardLevel.PLATINUM;
            earnedPoints = 1000;
        } else if (deliveredOrderCount >= 30) {
            rewardLevel = Reward.RewardLevel.GOLD;
            earnedPoints = 500;
        } else if (deliveredOrderCount >= 10) {
            rewardLevel = Reward.RewardLevel.SILVER;
            earnedPoints = 300;
        } else if (deliveredOrderCount >= 5) {
            rewardLevel = Reward.RewardLevel.BRONZE;
            earnedPoints = 100;
        } else {
            rewardLevel = Reward.RewardLevel.BRONZE; // Default level if less than 5 orders
            earnedPoints = 0;
        }

        customerReward.setPoints(earnedPoints);
        customerReward.setRewardLevel(rewardLevel);
        customerReward.setLastUpdated(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        rewardRepo.save(customerReward);

        return modelMapper.map(customerReward, RewardDTO.class);
    }
}