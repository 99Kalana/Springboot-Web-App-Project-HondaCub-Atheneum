package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.ReviewDTO;
import org.example.hondasupercub.dto.SparePartDTO;

import java.util.List;

public interface CustomerReviewService {
    List<SparePartDTO> searchSparePartsByName(String partName);
    SparePartDTO getSparePartDetails(int partId);
    List<ReviewDTO> getReviewsBySparePart(int partId);
    ReviewDTO addReview(ReviewDTO reviewDTO, String authorizationHeader);
}