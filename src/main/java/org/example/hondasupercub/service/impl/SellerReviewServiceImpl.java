package org.example.hondasupercub.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.hondasupercub.dto.ReviewDTO;
import org.example.hondasupercub.entity.Review;
import org.example.hondasupercub.repo.SellerReviewRepo;
import org.example.hondasupercub.service.SellerReviewService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SellerReviewServiceImpl implements SellerReviewService {

    @Autowired
    private SellerReviewRepo reviewRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public List<ReviewDTO> getReviewsBySeller(String authorizationHeader) {
        int sellerId = extractSellerIdFromToken(authorizationHeader);
        List<Review> reviews = reviewRepo.findReviewsBySellerId(sellerId);
        return reviews.stream()
                .map(review -> modelMapper.map(review, ReviewDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ReviewDTO replyToReview(int reviewId, String replyComment, String authorizationHeader) {
        int sellerId = extractSellerIdFromToken(authorizationHeader);
        Review review = reviewRepo.findByReviewId(reviewId);

        if (review != null && review.getSparePart().getSeller().getUserId() == sellerId) {
            review.setComment(review.getComment() + "\nSeller Reply: " + replyComment);
            Review updatedReview = reviewRepo.save(review);
            return modelMapper.map(updatedReview, ReviewDTO.class);
        }
        return null;
    }

    private int extractSellerIdFromToken(String authorizationHeader) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authorizationHeader.substring(7)).getBody();
        return (Integer) claims.get("userId");
    }
}