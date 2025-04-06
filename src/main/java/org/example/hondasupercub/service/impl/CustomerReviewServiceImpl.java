package org.example.hondasupercub.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.hondasupercub.dto.ReviewDTO;
import org.example.hondasupercub.dto.SparePartDTO;
import org.example.hondasupercub.dto.SparePartImageDTO;
import org.example.hondasupercub.entity.Review;
import org.example.hondasupercub.entity.SparePart;
import org.example.hondasupercub.entity.SparePartImage;
import org.example.hondasupercub.entity.User;
import org.example.hondasupercub.repo.*;
import org.example.hondasupercub.service.CustomerReviewService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerReviewServiceImpl implements CustomerReviewService {

    @Autowired
    private CustomerReviewRepo reviewRepo;

    @Autowired
    private CustomerReviewUserRepo userRepo;

    @Autowired
    private CustomerReviewSparePartRepo sparePartRepo;

    @Autowired
    private CustomerReviewSparePartImageRepo sparePartImageRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public List<SparePartDTO> searchSparePartsByName(String partName) {
        List<SparePart> spareParts = sparePartRepo.findByPartNameContainingIgnoreCase(partName);
        return spareParts.stream()
                .map(this::mapSparePartToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SparePartDTO getSparePartDetails(int partId) {
        SparePart sparePart = sparePartRepo.findById(partId).orElse(null);
        if (sparePart == null) {
            return null;
        }
        return mapSparePartToDTO(sparePart);
    }

    @Override
    public List<ReviewDTO> getReviewsBySparePart(int partId) {
        SparePart sparePart = sparePartRepo.findById(partId).orElse(null);
        if (sparePart == null) {
            return List.of();
        }
        List<Review> reviews = reviewRepo.findBySparePart(sparePart);
        return reviews.stream()
                .map(this::mapReviewToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewDTO addReview(ReviewDTO reviewDTO, String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        User user = userRepo.findByEmail(email);
        SparePart sparePart = sparePartRepo.findById(reviewDTO.getSparePartId()).orElse(null);

        if (user == null || sparePart == null) {
            return null;
        }

        Review review = modelMapper.map(reviewDTO, Review.class);
        review.setUser(user);
        review.setSparePart(sparePart);
        review.setReviewDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        Review savedReview = reviewRepo.save(review);
        return modelMapper.map(savedReview, ReviewDTO.class);
    }

    /*private SparePartDTO mapSparePartToDTO(SparePart sparePart) {
        SparePartDTO dto = modelMapper.map(sparePart, SparePartDTO.class);
        List<SparePartImage> images = sparePartImageRepo.findBySparePart(sparePart);
        List<SparePartImageDTO> imageDTOs = images.stream()
                .map(image -> modelMapper.map(image, SparePartImageDTO.class))
                .collect(Collectors.toList());
        dto.setImages(imageDTOs);
        return dto;
    }*/

    private SparePartDTO mapSparePartToDTO(SparePart sparePart) {
        SparePartDTO dto = modelMapper.map(sparePart, SparePartDTO.class);
        List<SparePartImage> images = sparePartImageRepo.findBySparePart(sparePart);
        List<SparePartImageDTO> imageDTOs = images.stream()
                .map(image -> {
                    SparePartImageDTO imageDTO = modelMapper.map(image, SparePartImageDTO.class);
                    imageDTO.setImageUrl("http://localhost:8080/images/" + image.getImageUrl()); // Prepend the base URL
                    return imageDTO;
                })
                .collect(Collectors.toList());
        dto.setImages(imageDTOs);
        return dto;
    }

    private ReviewDTO mapReviewToDTO(Review review) {
        return modelMapper.map(review, ReviewDTO.class);
    }

    private String extractEmailFromToken(String authorizationHeader) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                Claims claims = Jwts.parser().setSigningKey(secretKey)
                        .parseClaimsJws(authorizationHeader.substring(7)).getBody();
                return claims.getSubject();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}