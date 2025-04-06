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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    private String fullName;

    @Column(unique = true)
    private String email;

    private String password;

    private String phone;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "qr_code", columnDefinition = "TEXT")
    private String qrCode;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews;

    @OneToMany(mappedBy = "user")
    private List<Forum> forums;

    @OneToMany(mappedBy = "user")
    private List<Blog> blogs;

    @OneToMany(mappedBy = "user")
    private List<Reward> reward;

    @OneToMany(mappedBy = "user")
    private List<Transaction> transactions;

    public enum UserRole {
        ADMIN, SELLER, CUSTOMER
    }

    public enum UserStatus {
        ACTIVE, INACTIVE
    }
}
