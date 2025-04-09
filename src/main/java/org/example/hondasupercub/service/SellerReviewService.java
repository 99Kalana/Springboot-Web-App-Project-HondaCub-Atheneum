package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.ReviewDTO;

import java.util.List;
public interface SellerReviewService {
    List<ReviewDTO> getReviewsBySeller(String authorizationHeader);

    List<ReviewDTO> getReviewsBySellerAndPartId(String authorizationHeader, Integer partId); // New method

    ReviewDTO replyToReview(int reviewId, String replyComment, String authorizationHeader);
}