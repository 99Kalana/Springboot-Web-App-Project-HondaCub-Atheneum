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
@Table(name = "vin_parts")
@IdClass(VinPartsId.class)
public class VinParts {
    @Id
    @ManyToOne
    @JoinColumn(name = "vin_id")
    private VinHistory vinHistory;

    @Id
    @ManyToOne
    @JoinColumn(name = "part_id")
    private SparePart sparePart;
}