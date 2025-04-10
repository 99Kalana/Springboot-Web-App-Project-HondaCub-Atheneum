package org.example.hondasupercub.service;

import com.itextpdf.text.DocumentException;
import org.example.hondasupercub.dto.ReviewDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
public interface SellerReviewService {
    List<ReviewDTO> getReviewsBySeller(String authorizationHeader);

    List<ReviewDTO> getReviewsBySellerAndPartId(String authorizationHeader, Integer partId); // New method

    ReviewDTO replyToReview(int reviewId, String replyComment, String authorizationHeader);

    ByteArrayInputStream generateSellerReviewPdfReport(String authorizationHeader) throws DocumentException, IOException;
}