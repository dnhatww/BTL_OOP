package com.tailieuptit.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    @Column(name = "slug", length = 50, nullable = false, unique = true)
    private String slug;

    // Quan hệ N-N: 'mappedBy' trỏ đến tên biến 'tags' trong Document.java
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<Document> documents = new HashSet<>();
}