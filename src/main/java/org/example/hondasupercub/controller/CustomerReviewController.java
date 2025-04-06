package org.example.hondasupercub.controller;

import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.dto.ReviewDTO;
import org.example.hondasupercub.dto.SparePartDTO;
import org.example.hondasupercub.service.CustomerReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer/reviews")
public class CustomerReviewController {

    @Autowired
    private CustomerReviewService reviewService;

    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchSpareParts(@RequestParam String partName) {
        List<SparePartDTO> spareParts = reviewService.searchSparePartsByName(partName);
        ResponseDTO responseDTO = new ResponseDTO(200, "Spare parts found.", spareParts);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/{partId}/details")
    public ResponseEntity<ResponseDTO> getSparePartDetails(@PathVariable int partId) {
        SparePartDTO sparePart = reviewService.getSparePartDetails(partId);
        if (sparePart == null) {
            return new ResponseEntity<>(new ResponseDTO(404, "Spare part not found.", null), HttpStatus.NOT_FOUND);
        }
        ResponseDTO responseDTO = new ResponseDTO(200, "Spare part details.", sparePart);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/{partId}/reviews")
    public ResponseEntity<ResponseDTO> getReviews(@PathVariable int partId) {
        List<ReviewDTO> reviews = reviewService.getReviewsBySparePart(partId);
        ResponseDTO responseDTO = new ResponseDTO(200, "Reviews found.", reviews);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> addReview(@RequestBody ReviewDTO reviewDTO, @RequestHeader("Authorization") String authorizationHeader) {
        ReviewDTO savedReview = reviewService.addReview(reviewDTO, authorizationHeader);
        if (savedReview == null) {
            return new ResponseEntity<>(new ResponseDTO(400, "Failed to add review.", null), HttpStatus.BAD_REQUEST);
        }
        ResponseDTO responseDTO = new ResponseDTO(201, "Review added successfully.", savedReview);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
}