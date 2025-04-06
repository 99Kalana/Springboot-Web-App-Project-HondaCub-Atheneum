package org.example.hondasupercub.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SparePartImageDTO {

    private int imageId;

    private int sparePartId;  // Store only the sparePartId, not the full SparePart entity

    private String imageUrl;
}
