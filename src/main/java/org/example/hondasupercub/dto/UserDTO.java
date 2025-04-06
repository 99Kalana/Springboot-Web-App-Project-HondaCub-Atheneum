package org.example.hondasupercub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {

    private int userId;

    private String fullName;

    private String email;

    private String password;  // Avoid including passwords in DTOs for security reasons

    private String phone;

    private String role;  // Can be String for simpler handling of enum as a text

    private String qrCode;

    private String status = "ACTIVE";  // Default value set to "ACTIVE"

    private List<Integer> orderIds;  // Store only the IDs, not full entity relationships
    private List<Integer> reviewIds;
    private List<Integer> forumIds;
    private List<Integer> blogIds;
    private List<Integer> rewardIds;
    private List<Integer> transactionIds;

    private String currentPassword;
}
