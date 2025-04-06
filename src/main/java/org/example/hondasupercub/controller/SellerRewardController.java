package org.example.hondasupercub.controller;

import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.dto.RewardDTO;
import org.example.hondasupercub.service.SellerRewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seller/rewards")
public class SellerRewardController {

    @Autowired
    private SellerRewardService sellerRewardService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getRewards(@RequestHeader("Authorization") String authorizationHeader) {
        List<RewardDTO> rewards = sellerRewardService.getRewardsBySeller(authorizationHeader);
        return new ResponseEntity<>(new ResponseDTO(HttpStatus.OK.value(), "Rewards fetched successfully", rewards), HttpStatus.OK);
    }

    @GetMapping("/{rewardId}")
    public ResponseEntity<ResponseDTO> getRewardDetails(
            @PathVariable int rewardId,
            @RequestHeader("Authorization") String authorizationHeader) {

        RewardDTO reward = sellerRewardService.getRewardDetails(rewardId, authorizationHeader);
        if (reward != null) {
            return new ResponseEntity<>(new ResponseDTO(HttpStatus.OK.value(), "Reward details fetched successfully", reward), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseDTO(HttpStatus.NOT_FOUND.value(), "Reward not found or not belonging to seller", null), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{rewardId}/redeem")
    public ResponseEntity<ResponseDTO> redeemPoints(
            @PathVariable int rewardId,
            @RequestParam("pointsToRedeem") int pointsToRedeem,
            @RequestHeader("Authorization") String authorizationHeader) {

        RewardDTO redeemedReward = sellerRewardService.redeemPoints(rewardId, pointsToRedeem, authorizationHeader);
        if (redeemedReward != null) {
            return new ResponseEntity<>(new ResponseDTO(HttpStatus.OK.value(), "Points redeemed successfully", redeemedReward), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseDTO(HttpStatus.BAD_REQUEST.value(), "Insufficient points or reward not found", null), HttpStatus.BAD_REQUEST);
        }
    }
}