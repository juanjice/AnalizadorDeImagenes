package com.example.image_analyzer.repository;

import com.example.image_analyzer.entity.ImageTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageTagRepository extends JpaRepository<ImageTag,Long> {
}
