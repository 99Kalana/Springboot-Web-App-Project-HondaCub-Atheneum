package org.example.hondasupercub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class BlogDTO {

    private int blogId;

    private int userId; // Store only userId instead of full User object

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotBlank(message = "Content cannot be empty")
    private String content;

    private String imageUrl;

    private String status = "PUBLISHED";

    private String createdAt;
}
