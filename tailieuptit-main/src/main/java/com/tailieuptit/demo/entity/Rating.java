package com.tailieuptit.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ratings",
        // Ánh xạ ràng buộc UNIQUE KEY `uq_user_doc_rating` (`user_id`, `doc_id`)
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "doc_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long id;

    // Dùng 'short' cho TINYINT
    @Column(name = "score", nullable = false)
    private short score; // (1-5 sao)

    // N-1 với Document
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_id", nullable = false)
    private Document document;

    // N-1 với User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}