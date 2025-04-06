package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRewardRepo extends JpaRepository<Reward, Integer> {

    @Query("SELECT r FROM Reward r JOIN r.user u WHERE u.userId = :sellerId AND u.role = 'SELLER'")
    List<Reward> findRewardsBySellerId(@Param("sellerId") int sellerId);

    @Query("SELECT r FROM Reward r JOIN r.user u WHERE r.rewardId = :rewardId AND u.userId = :sellerId AND u.role = 'SELLER'")
    Optional<Reward> findRewardByIdAndSellerId(@Param("rewardId") int rewardId, @Param("sellerId") int sellerId);



}