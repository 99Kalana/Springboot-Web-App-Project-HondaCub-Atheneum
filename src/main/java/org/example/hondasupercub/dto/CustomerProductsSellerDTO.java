package org.example.hondasupercub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProductsSellerDTO {
    private int userId;
    private String fullName;
}