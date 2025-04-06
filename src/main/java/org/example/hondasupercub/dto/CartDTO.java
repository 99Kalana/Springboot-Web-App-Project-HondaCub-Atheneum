package org.example.hondasupercub.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CartDTO {

    private int cartId;

    private int userId; // Store only the userId, not the full User entity

    private int sparePartId; // Store only the sparePartId, not the full SparePart entity

    private int quantity;

    private String addedAt;

    private SparePartDTO sparePart;
}
