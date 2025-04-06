package org.example.hondasupercub.controller;

import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.dto.RewardDTO;
import org.example.hondasupercub.service.impl.AdminRewardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/adminrewards")
public class AdminRewardController {

    @Autowired
    private AdminRewardServiceImpl rewardService;

    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAllRewards() {
        List<RewardDTO> rewards = rewardService.getAllRewards();
        return new ResponseEntity<>(new ResponseDTO(200, "Rewards retrieved successfully", rewards), HttpStatus.OK);
    }

    @GetMapping("/get/{rewardId}")
    public ResponseEntity<ResponseDTO> getRewardById(@PathVariable int rewardId) {
        RewardDTO reward = rewardService.getRewardById(rewardId);
        if (reward != null) {
            return new ResponseEntity<>(new ResponseDTO(200, "Reward retrieved successfully", reward), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseDTO(404, "Reward not found", null), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchRewards(@RequestParam String query) {
        List<RewardDTO> rewards = rewardService.searchRewards(query);
        return new ResponseEntity<>(new ResponseDTO(200, "Rewards found", rewards), HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<ResponseDTO> filterRewardsByLevel(@RequestParam String level) {
        List<RewardDTO> rewards = rewardService.filterRewardsByLevel(level);
        return new ResponseEntity<>(new ResponseDTO(200, "Rewards filtered", rewards), HttpStatus.OK);
    }

    @PutMapping("/update/{rewardId}")
    public ResponseEntity<ResponseDTO> updateReward(@PathVariable int rewardId, @RequestBody RewardDTO rewardDTO) {
        RewardDTO updatedReward = rewardService.updateReward(rewardId, rewardDTO.getPoints());
        if (updatedReward != null) {
            return new ResponseEntity<>(new ResponseDTO(200, "Reward updated successfully", updatedReward), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseDTO(404, "Reward not found", null), HttpStatus.NOT_FOUND);
        }
    }
}