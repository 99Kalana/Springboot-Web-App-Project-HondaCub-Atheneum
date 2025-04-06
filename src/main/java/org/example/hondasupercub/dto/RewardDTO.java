package org.example.hondasupercub.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RewardDTO {

    private int rewardId;

    private int userId;  // Store only the userId, not the full User entity

    private String fullName;

    private int points = 0;

    private int redeemedPoints = 0;

    private RewardLevel rewardLevel = RewardLevel.BRONZE;

    private String lastUpdated;

    public enum RewardLevel {
        BRONZE, SILVER, GOLD, PLATINUM
    }
}
