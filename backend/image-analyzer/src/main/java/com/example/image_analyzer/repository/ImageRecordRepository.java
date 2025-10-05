package com.example.image_analyzer.repository;

import com.example.image_analyzer.entity.ImageRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageRecordRepository extends JpaRepository<ImageRecord, UUID> {
}
