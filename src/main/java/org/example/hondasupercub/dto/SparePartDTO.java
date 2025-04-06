package org.example.hondasupercub.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SparePartDTO {

    private int partId;

    private String partName;

    private String categoryName;

    private int categoryId;

    private double price;

    private int stock;

    private String description;

    private int sellerId;  // Store only the sellerId, not the full User entity

    private List<SparePartImageDTO> images;

}
