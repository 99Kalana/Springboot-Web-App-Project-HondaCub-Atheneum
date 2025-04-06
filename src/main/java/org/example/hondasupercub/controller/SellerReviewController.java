package org.example.hondasupercub.controller;

import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.dto.ReviewDTO;
import org.example.hondasupercub.service.SellerReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seller/reviews")
public class SellerReviewController {

    @Autowired
    private SellerReviewService sellerReviewService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getSellerReviews(@RequestHeader("Authorization") String authorizationHeader) {
        List<ReviewDTO> reviews = sellerReviewService.getReviewsBySeller(authorizationHeader);
        return new ResponseEntity<>(new ResponseDTO(HttpStatus.OK.value(), "Reviews fetched successfully", reviews), HttpStatus.OK);
    }

    @PutMapping("/{reviewId}/reply")
    public ResponseEntity<ResponseDTO> replyToReview(
            @PathVariable int reviewId,
            @RequestParam("replyComment") String replyComment,
            @RequestHeader("Authorization") String authorizationHeader) {

        ReviewDTO updatedReview = sellerReviewService.replyToReview(reviewId, replyComment, authorizationHeader);
        if (updatedReview != null) {
            return new ResponseEntity<>(new ResponseDTO(HttpStatus.OK.value(), "Reply added successfully", updatedReview), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseDTO(HttpStatus.NOT_FOUND.value(), "Review not found or not belonging to seller", null), HttpStatus.NOT_FOUND);
        }
    }
}