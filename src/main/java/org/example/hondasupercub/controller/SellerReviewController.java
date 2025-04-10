package org.example.hondasupercub.controller;

import com.itextpdf.text.DocumentException;
import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.dto.ReviewDTO;
import org.example.hondasupercub.service.SellerReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/seller/reviews")
public class SellerReviewController {

    @Autowired
    private SellerReviewService sellerReviewService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getSellerReviews(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(value = "partId", required = false) Integer partId) {

        List<ReviewDTO> reviews;
        if (partId != null) {
            reviews = sellerReviewService.getReviewsBySellerAndPartId(authorizationHeader, partId);
            if (reviews.isEmpty()) {
                return new ResponseEntity<>(new ResponseDTO(HttpStatus.NOT_FOUND.value(), "No reviews found for the specified part ID", null), HttpStatus.NOT_FOUND);
            }
        } else {
            reviews = sellerReviewService.getReviewsBySeller(authorizationHeader);
        }
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


    @GetMapping("/report/download")
    public ResponseEntity<InputStreamResource> downloadSellerReviewReport(
            @RequestHeader("Authorization") String authorizationHeader) throws IOException, DocumentException {
        ByteArrayInputStream pdfReport = sellerReviewService.generateSellerReviewPdfReport(authorizationHeader);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=seller_reviews_report.pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(pdfReport));
    }

}