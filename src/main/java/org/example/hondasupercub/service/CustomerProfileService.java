package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.UserDTO;

public interface CustomerProfileService {
    int updateUserProfile(UserDTO userDTO);

    UserDTO getUserProfile(Integer userId);
}