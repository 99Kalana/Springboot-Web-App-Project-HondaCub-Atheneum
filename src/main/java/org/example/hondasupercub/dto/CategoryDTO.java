package org.example.hondasupercub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CategoryDTO {

    private int categoryId;

    //@NotBlank(message = "Category name cannot be empty")
    private String categoryName;

    private String description;

    private List<Integer> sparePartIds; // Store only the IDs of the related spare parts
}
