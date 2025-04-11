package org.example.hondasupercub.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ModelArchiveDTO {

    private int modelId;

    @NotBlank(message = "Model name cannot be blank")
    @Size(min = 2, max = 100, message = "Model name must be between 2 and 100 characters")
    private String modelName;

    @NotNull(message = "Model year cannot be null")
    @Min(value = 1958, message = "Model year must be at least 1958")
    @Max(value = 2050, message = "Model year cannot exceed 2050")
    private int modelYear;

    @NotBlank(message = "Engine capacity cannot be blank")
    @Size(max = 20, message = "Engine capacity cannot exceed 20 characters")
    private String engineCapacity;

    @Size(max = 20, message = "Top speed cannot exceed 20 characters")
    private String topSpeed;

    @Size(max = 20, message = "Fuel consumption cannot exceed 20 characters")
    private String fuelConsumption;

    @Size(max = 100, message = "Production years cannot exceed 100 characters")
    private String productionYears;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private List<Integer> modelImageIds;  // Store only the IDs of related model images

    private List<Integer> vinHistoryIds;  // Store only the IDs of related VIN histories


    private List<ModelImageDTO> modelImages; // Add this new field

    @Data
    public static class ModelImageDTO {
        private int imageId;
        private String imageUrl;
    }
}
