package org.example.hondasupercub.controller;

import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.dto.SparePartDTO;
import org.example.hondasupercub.service.CustomerHomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer/home")
public class CustomerHomeController {

    @Autowired
    private CustomerHomeService customerHomeService;

    @GetMapping("/featured-products")
    public ResponseEntity<ResponseDTO> getFeaturedProducts(@RequestHeader("Authorization") String authorizationHeader) {
        List<SparePartDTO> featuredProducts = customerHomeService.getFeaturedProductsWithHighRating(authorizationHeader);

        if (featuredProducts == null) {
            ResponseDTO responseDTO = new ResponseDTO(HttpStatus.UNAUTHORIZED.value(), "Unauthorized: Invalid or missing token.", null);
            return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
        }

        if (featuredProducts.isEmpty()) {
            ResponseDTO responseDTO = new ResponseDTO(HttpStatus.NOT_FOUND.value(), "No featured products found.", null);
            return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
        }

        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK.value(), "Featured products retrieved successfully.", featuredProducts);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}