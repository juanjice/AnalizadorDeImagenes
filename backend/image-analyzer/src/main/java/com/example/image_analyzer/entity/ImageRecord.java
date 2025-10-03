package com.example.image_analyzer.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="image")
@Data
public class ImageRecord {
    @Id
    private UUID id;

    @Column(name="image_key",nullable = false, unique = true)
    private String imageKey;

    @Column(name="content_type", nullable = false)
    private String contentType;

    @Column(name="size_Bytes", nullable=false)
    private long sizeBytes;

    @Column(nullable = false)
    private ImageStatus status;

    private Instant createdAt;
    private Instant updatedAt;

    @OneToMany(mappedBy = "image", fetch = FetchType.LAZY)
    private List<ImageTag> tags;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
}

