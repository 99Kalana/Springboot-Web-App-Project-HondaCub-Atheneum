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
@Table(name = "vin_history")
public class VinHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int vinId;

    private String vinNumber;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private ModelArchive modelArchive;

    private String compatibilityNotes;


}
