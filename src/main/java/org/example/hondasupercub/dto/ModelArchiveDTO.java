package org.example.hondasupercub.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ModelArchiveDTO {

    private int modelId;

    private String modelName;

    private int modelYear;

    private String engineCapacity;

    private String topSpeed;

    private String fuelConsumption;

    private String productionYears;

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
