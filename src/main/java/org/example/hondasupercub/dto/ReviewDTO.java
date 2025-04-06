package org.example.hondasupercub.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ReviewDTO {

    private int reviewId;

    private int userId;  // Store only the userId, not the full User entity

    private int sparePartId;  // Store only the sparePartId, not the full SparePart entity

    private float rating;

    private String comment;

    private String reviewDate;
}
