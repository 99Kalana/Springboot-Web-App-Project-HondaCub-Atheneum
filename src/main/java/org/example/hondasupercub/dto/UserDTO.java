package org.example.hondasupercub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {

    private int userId;

    @NotBlank(message = "Full name cannot be blank")
    @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;  // Avoid including passwords in DTOs for security reasons

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be a 10-digit number")
    private String phone;

    @NotBlank(message = "Role cannot be blank")
    private String role;  // Can be String for simpler handling of enum as a text

    private String qrCode;

    @NotBlank(message = "Status cannot be blank")
    private String status = "ACTIVE";  // Default value set to "ACTIVE"

    private List<Integer> orderIds;  // Store only the IDs, not full entity relationships
    private List<Integer> reviewIds;
    private List<Integer> forumIds;
    private List<Integer> blogIds;
    private List<Integer> rewardIds;
    private List<Integer> transactionIds;

    private String currentPassword;
}
