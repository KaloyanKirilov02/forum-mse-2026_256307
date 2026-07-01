package com.mse.edu.forum.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "posts",
        uniqueConstraints = @UniqueConstraint(name = "uq_posts_title", columnNames = "title")
)
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false, length = 10_000)
    private String content;

    @Column(name = "author_id")
    private Long authorId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant modifiedAt;

    @Column(nullable = false)
    private Long viewsCount = 0L;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (modifiedAt == null) {
            modifiedAt = now;
        }

        if (viewsCount == null) {
            viewsCount = 0L;
        }
    }

    @PreUpdate
    void onUpdate() {
        modifiedAt = Instant.now();
    }
}