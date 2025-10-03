package com.example.image_analyzer.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ImageTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "image_id", nullable = false)
    private ImageRecord image;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private double confidence;

}
