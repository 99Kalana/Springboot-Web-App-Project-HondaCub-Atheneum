package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.UserDTO;

public interface AdminProfileService {
    UserDTO getUserProfile(Integer userId);
    int updateUserProfile(UserDTO userDTO);
    String getNewToken();
}