// AdminProfileServiceImpl.java
package org.example.hondasupercub.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.example.hondasupercub.dto.UserDTO;
import org.example.hondasupercub.entity.User;
import org.example.hondasupercub.repo.AdminProfileUserRepo;
import org.example.hondasupercub.service.AdminProfileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminProfileServiceImpl implements AdminProfileService {

    @Autowired
    private AdminProfileUserRepo adminProfileUserRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    private String newToken;

    @Override
    public UserDTO getUserProfile(Integer userId) {
        Optional<User> userOptional = adminProfileUserRepo.findById(userId);
        if (userOptional.isPresent()) {
            return modelMapper.map(userOptional.get(), UserDTO.class);
        }
        return null;
    }

    @Override
    public int updateUserProfile(UserDTO userDTO) {
        Optional<User> userOptional = adminProfileUserRepo.findById(userDTO.getUserId());
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            if (!existingUser.getEmail().equals(userDTO.getEmail()) && adminProfileUserRepo.findByEmail(userDTO.getEmail()).isPresent()) {
                return -1; // Email already exists
            }

            if (userDTO.getCurrentPassword() != null && !userDTO.getCurrentPassword().isEmpty()) {
                if (!passwordEncoder.matches(userDTO.getCurrentPassword(), existingUser.getPassword())) {
                    return -2; // Incorrect current password
                }
            }

            modelMapper.map(userDTO, existingUser);

            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                // Generate and set QR code
                try {
                    String qrCodeData = generateQRCodeBase64(userDTO.getEmail(), userDTO.getPassword());
                    existingUser.setQrCode(qrCodeData);
                } catch (WriterException | IOException e) {
                    e.printStackTrace(); // Handle exception properly
                }
            }

            adminProfileUserRepo.save(existingUser);
            this.newToken = "yourNewToken"; // Replace with your token generation logic
            return 1; // Success
        }
        return 0; // User not found
    }

    @Override
    public String getNewToken() {
        return this.newToken;
    }

    private String generateQRCodeBase64(String email, String password) throws WriterException, IOException {
        String data = "email:" + email + ",password:" + password;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 256, 256, hints);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngData);
    }
}