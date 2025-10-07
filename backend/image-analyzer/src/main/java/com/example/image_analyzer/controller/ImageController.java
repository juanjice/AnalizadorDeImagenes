package com.example.image_analyzer.controller;

import com.example.image_analyzer.dto.ImageDetailsDto;
import com.example.image_analyzer.entity.ImageRecord;
import com.example.image_analyzer.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping(value="/analyze",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> analyze(@RequestPart("file") MultipartFile file){
        ImageRecord imageRecord = imageService.submitForAnalysis(file);
        return new ResponseEntity<>(imageRecord, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImageDetailsDto> getById(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(imageService.getById(id), HttpStatus.OK);
                //imageRecordRepository.findById(id)
                //.map(rec -> ResponseEntity.ok(toDto(rec)))
                //.orElseGet(() -> ResponseEntity.notFound().build());
    }

}


