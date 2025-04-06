package org.example.hondasupercub.controller;

import jakarta.validation.Valid;
import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.dto.UserDTO;
import org.example.hondasupercub.service.SellerProfileService;
import org.example.hondasupercub.service.impl.SellerProfileServiceImpl; // Import the service implementation
import org.example.hondasupercub.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("api/v1/seller/profile")
public class SellerProfileController {

    private final SellerProfileService sellerProfileService;
    private final JwtUtil jwtUtil;

    public SellerProfileController(SellerProfileService sellerProfileService, JwtUtil jwtUtil) {
        this.sellerProfileService = sellerProfileService;
        this.jwtUtil = jwtUtil;
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDTO> updateSellerProfile(@RequestBody @Valid UserDTO userDTO,
                                                           @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.substring(7);

            Claims claims = jwtUtil.getUserRoleCodeFromToken(token);
            Integer userId = (Integer) claims.get("userId");

            userDTO.setUserId(userId);

            int res = sellerProfileService.updateUserProfile(userDTO);

            String newToken = ((SellerProfileServiceImpl) sellerProfileService).getNewToken(); // Get the new token

            if (res == 1) {
                return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK.value(), "Profile updated successfully.", newToken));
            } else if (res == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(HttpStatus.NOT_FOUND.value(), "User not found.", null));
            } else if (res == -1) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ResponseDTO(HttpStatus.CONFLICT.value(), "Email already exists.", null));
            } else if (res == -2) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseDTO(HttpStatus.UNAUTHORIZED.value(), "Incorrect current password.", null));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to update profile.", null));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getSellerProfile(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.substring(7);

            Claims claims = jwtUtil.getUserRoleCodeFromToken(token);
            Integer userId = (Integer) claims.get("userId");

            UserDTO userDTO = sellerProfileService.getUserProfile(userId);

            if (userDTO != null) {
                return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK.value(), "Profile retrieved successfully.", userDTO));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(HttpStatus.NOT_FOUND.value(), "User not found.", null));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null));
        }
    }
}