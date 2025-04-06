package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellerReviewRepo extends JpaRepository<Review, Integer> {

    @Query("SELECT r FROM Review r JOIN r.sparePart sp JOIN sp.seller s WHERE s.userId = :sellerId")
    List<Review> findReviewsBySellerId(@Param("sellerId") int sellerId);

    Review findByReviewId(int reviewId);
}