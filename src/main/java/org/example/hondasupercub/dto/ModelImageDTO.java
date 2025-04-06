package org.example.hondasupercub.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ModelImageDTO {

    private int imageId;

    private int modelArchiveId;  // Store only the modelArchiveId, not the full ModelArchive entity

    private String imageUrl;
}
