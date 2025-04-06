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
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int categoryId;

    @Column(unique = true)
    private String categoryName;

    private String description;

    @OneToMany(mappedBy = "category")
    private List<SparePart> spareParts;
}
