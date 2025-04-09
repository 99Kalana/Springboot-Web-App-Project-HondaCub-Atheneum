package org.example.hondasupercub.controller;

import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.dto.RewardDTO;
import org.example.hondasupercub.service.CustomerRewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer/rewards")
public class CustomerRewardController {

    @Autowired
    private CustomerRewardService customerRewardService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getCustomerRewards(@RequestHeader("Authorization") String authorizationHeader) {
        RewardDTO rewardDTO = customerRewardService.getCustomerRewardDetails(authorizationHeader);
        if (rewardDTO != null) {
            return new ResponseEntity<>(new ResponseDTO(HttpStatus.OK.value(), "Customer reward details fetched successfully", rewardDTO), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseDTO(HttpStatus.NOT_FOUND.value(), "Customer reward details not found", null), HttpStatus.NOT_FOUND);
        }
    }
}