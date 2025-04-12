package org.example.hondasupercub.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SparePartDTO {

    private int partId;

    @NotBlank(message = "Part name cannot be blank")
    @Size(min = 2, max = 255, message = "Part name must be between 2 and 255 characters")
    private String partName;

    @NotBlank(message = "Category name cannot be blank")
    private String categoryName;

    @NotNull(message = "Category ID cannot be null")
    @Min(value = 1, message = "Category ID must be a positive value")
    private int categoryId;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private double price;

    @NotNull(message = "Stock cannot be null")
    @Min(value = 0, message = "Stock cannot be negative")
    private int stock;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Seller ID cannot be null")
    @Min(value = 1, message = "Seller ID must be a positive value")
    private int sellerId;  // Store only the sellerId, not the full User entity

    private List<SparePartImageDTO> images;

}
