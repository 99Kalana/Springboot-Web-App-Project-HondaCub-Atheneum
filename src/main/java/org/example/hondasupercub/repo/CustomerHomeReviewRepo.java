package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Review;
import org.example.hondasupercub.entity.SparePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerHomeReviewRepo extends JpaRepository<Review, Integer> {
    List<Review> findBySparePart(SparePart sparePart);
}