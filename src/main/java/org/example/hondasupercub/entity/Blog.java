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
@Table(name = "blogs")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int blogId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    private String content;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private BlogStatus status = BlogStatus.PUBLISHED;

    private String createdAt;

    public enum BlogStatus {
        PUBLISHED, DRAFT, ARCHIVED
    }
}
