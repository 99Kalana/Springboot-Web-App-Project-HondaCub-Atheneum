package org.example.hondasupercub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Data
@Entity
@Table(name = "model_archive")
public class ModelArchive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int modelId;

    private String modelName;

    private int modelYear;

    private String engineCapacity;

    private String topSpeed;

    private String fuelConsumption;

    private String productionYears;

    private String description;

    @OneToMany(mappedBy = "modelArchive")
    private List<ModelImage> modelImages;

    //@OneToMany(mappedBy = "modelArchive")
    @OneToMany(mappedBy = "modelArchive", fetch = FetchType.EAGER)
    private List<VinHistory> vinHistories;
}
