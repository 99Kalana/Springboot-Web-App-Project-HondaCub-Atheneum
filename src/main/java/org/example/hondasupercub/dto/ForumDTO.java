package org.example.hondasupercub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ForumDTO {

    private int forumId;

    private int userId; // Store only the userId, not the full User entity

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotBlank(message = "Content cannot be empty")
    private String content;

    private String status = "ACTIVE";

    private String createdAt;
}
