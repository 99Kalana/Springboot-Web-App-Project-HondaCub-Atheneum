package org.example.hondasupercub.service.impl;

import org.example.hondasupercub.dto.UserDTO;
import org.example.hondasupercub.entity.User;
import org.example.hondasupercub.repo.UserRepository;
import org.example.hondasupercub.service.CustomerProfileService;
import org.example.hondasupercub.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CustomerProfileServiceImpl implements CustomerProfileService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private String newToken; // Class variable to store the new token

    @Autowired
    public CustomerProfileServiceImpl(UserRepository userRepository, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public int updateUserProfile(UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findById(userDTO.getUserId());

        if (optionalUser.isEmpty()) {
            return 0; // Indicate user not found
        }

        User user = optionalUser.get();

        // Password Verification
        if (userDTO.getCurrentPassword() != null && !userDTO.getCurrentPassword().isEmpty()) {
            if (!passwordEncoder.matches(userDTO.getCurrentPassword(), user.getPassword())) {
                return -2; // Indicate incorrect current password
            }

            // If current password is correct, update the password
            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }
        } else {
            // If current password is not provided, do not update the password
            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }
        }

        // Update other fields
        if (userDTO.getFullName() != null) {
            user.setFullName(userDTO.getFullName());
        }

        if (userDTO.getPhone() != null) {
            user.setPhone(userDTO.getPhone());
        }


        String qrCodeData = user.getEmail() + ":" + user.getPassword();
        user.setQrCode(qrCodeData);

        userRepository.save(user);

        // Update the userDTO with the updated user details before generating the token
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(String.valueOf(user.getRole()));
        userDTO.setUserId(user.getUserId());

        this.newToken = jwtUtil.generateToken(userDTO); // Store the new token

        return 1; // Indicate user updated successfully
    }

    @Override
    public UserDTO getUserProfile(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            return modelMapper.map(optionalUser.get(), UserDTO.class);
        } else {
            return null;
        }
    }

    public String getNewToken() {
        return newToken;
    }
}