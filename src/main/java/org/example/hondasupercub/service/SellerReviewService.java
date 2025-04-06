package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.ReviewDTO;

import java.util.List;
public interface SellerReviewService {
    List<ReviewDTO> getReviewsBySeller(String authorizationHeader);

    ReviewDTO replyToReview(int reviewId, String replyComment, String authorizationHeader);

}
