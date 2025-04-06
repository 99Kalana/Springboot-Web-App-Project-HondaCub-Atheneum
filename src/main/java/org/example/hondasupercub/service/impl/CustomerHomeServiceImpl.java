// CustomerHomeServiceImpl.java
package org.example.hondasupercub.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.hondasupercub.dto.SparePartDTO;
import org.example.hondasupercub.dto.SparePartImageDTO;
import org.example.hondasupercub.entity.Review;
import org.example.hondasupercub.entity.SparePart;
import org.example.hondasupercub.repo.CustomerHomeReviewRepo;
import org.example.hondasupercub.repo.CustomerHomeSparePartRepo;
import org.example.hondasupercub.service.CustomerHomeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerHomeServiceImpl implements CustomerHomeService {

    @Autowired
    private CustomerHomeSparePartRepo sparePartRepo;

    @Autowired
    private CustomerHomeReviewRepo reviewRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${jwt.secret}")
    private String secretKey;

    private Integer extractUserIdFromToken(String authorizationHeader) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(authorizationHeader.substring(7)) // Remove "Bearer " prefix
                    .getBody();
            return (Integer) claims.get("userId");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<SparePartDTO> getFeaturedProductsWithHighRating(String authorizationHeader) {
        Integer userId = extractUserIdFromToken(authorizationHeader);

        if (userId == null) {
            return null; // Indicate unauthorized access
        }

        List<SparePart> allSpareParts = sparePartRepo.findAll();

        return allSpareParts.stream()
                .filter(sparePart -> {
                    List<Review> reviews = reviewRepo.findBySparePart(sparePart);
                    if (reviews.isEmpty()) {
                        return false;
                    }
                    double averageRating = reviews.stream()
                            .mapToDouble(Review::getRating)
                            .average()
                            .orElse(0.0);
                    return averageRating >= 4.0;
                })
                .map(this::convertToSparePartDTO)
                .collect(Collectors.toList());
    }

    private SparePartDTO convertToSparePartDTO(SparePart sparePart) {
        SparePartDTO sparePartDTO = modelMapper.map(sparePart, SparePartDTO.class);
        if (sparePart.getCategory() != null) {
            sparePartDTO.setCategoryName(sparePart.getCategory().getCategoryName());
            sparePartDTO.setCategoryId(sparePart.getCategory().getCategoryId());
        }
        if (sparePart.getImages() != null) {
            List<SparePartImageDTO> imageDTOs = sparePart.getImages().stream()
                    .map(image -> modelMapper.map(image, SparePartImageDTO.class))
                    .collect(Collectors.toList());
            sparePartDTO.setImages(imageDTOs);
        }
        if (sparePart.getSeller() != null) {
            sparePartDTO.setSellerId(sparePart.getSeller().getUserId());
        }
        return sparePartDTO;
    }
}