package org.example.hondasupercub.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Data
@Entity
@Table(name = "model_images")
public class ModelImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int imageId;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private ModelArchive modelArchive;

    private String imageUrl;
}
