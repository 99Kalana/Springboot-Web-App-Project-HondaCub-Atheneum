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
@Table(name = "spare_parts")
public class SparePart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int partId;

    private String partName;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private double price;

    private int stock;

    private String description;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    @OneToMany(mappedBy = "sparePart", fetch = FetchType.EAGER)
    private List<SparePartImage> images;

    @OneToMany(mappedBy = "sparePart")
    private List<Review> reviews;

    @OneToMany(mappedBy = "sparePart")
    private List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "sparePart")
    private List<Cart> cartItems;
}
