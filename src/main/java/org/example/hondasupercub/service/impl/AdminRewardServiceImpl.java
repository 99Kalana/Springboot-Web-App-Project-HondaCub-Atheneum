package org.example.hondasupercub.service.impl;

import org.example.hondasupercub.dto.RewardDTO;
import org.example.hondasupercub.entity.Reward;
import org.example.hondasupercub.repo.AdminRewardRepo;
import org.example.hondasupercub.service.AdminRewardService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminRewardServiceImpl implements AdminRewardService {

    @Autowired
    private AdminRewardRepo rewardRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public RewardDTO getRewardById(int rewardId) {
        Optional<Reward> reward = rewardRepo.findById(rewardId);
        return reward.map(value -> {
            RewardDTO dto = modelMapper.map(value, RewardDTO.class);
            dto.setFullName(value.getUser().getFullName()); // Add this line
            return dto;
        }).orElse(null);
    }

    @Override
    public List<RewardDTO> getAllRewards() {
        List<Reward> rewards = rewardRepo.findAll();
        return rewards.stream()
                .map(reward -> {
                    RewardDTO dto = modelMapper.map(reward, RewardDTO.class);
                    dto.setFullName(reward.getUser().getFullName()); // Add this line
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<RewardDTO> searchRewards(String query) {
        List<Reward> rewards = rewardRepo.findByUser_FullNameContainingIgnoreCase(query);
        return rewards.stream()
                .map(reward -> {
                    RewardDTO dto = modelMapper.map(reward, RewardDTO.class);
                    dto.setFullName(reward.getUser().getFullName()); // Add this line
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<RewardDTO> filterRewardsByLevel(String level) {
        Reward.RewardLevel rewardLevel = Reward.RewardLevel.valueOf(level.toUpperCase());
        List<Reward> rewards = rewardRepo.findByRewardLevel(rewardLevel);
        return modelMapper.map(rewards, new TypeToken<List<RewardDTO>>() {}.getType());
    }

    @Override
    public RewardDTO updateReward(int rewardId, int points) {
        Optional<Reward> rewardOptional = rewardRepo.findById(rewardId);
        if (rewardOptional.isPresent()) {
            Reward reward = rewardOptional.get();
            reward.setPoints(points);
            reward.setLastUpdated(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return modelMapper.map(rewardRepo.save(reward), RewardDTO.class);
        }
        return null;
    }
}