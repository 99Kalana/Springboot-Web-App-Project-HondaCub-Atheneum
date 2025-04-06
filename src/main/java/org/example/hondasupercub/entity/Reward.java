package org.example.hondasupercub.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Data
@Entity
@Table(name = "rewards")
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int rewardId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int points = 0;

    private int redeemedPoints = 0;

    @Enumerated(EnumType.STRING)
    private RewardLevel rewardLevel = RewardLevel.BRONZE;

    private String lastUpdated;

    public enum RewardLevel {
        BRONZE, SILVER, GOLD, PLATINUM
    }
}
