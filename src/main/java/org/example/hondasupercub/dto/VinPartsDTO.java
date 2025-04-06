package org.example.hondasupercub.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class VinPartsDTO {

    private int vinId;  // Represent the foreign key reference for VinHistory

    private int partId;  // Represent the foreign key reference for SparePart
}
