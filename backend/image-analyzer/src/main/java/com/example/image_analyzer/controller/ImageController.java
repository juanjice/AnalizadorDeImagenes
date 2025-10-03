package com.example.image_analyzer.controller;

import com.example.image_analyzer.entity.ImageRecord;
import com.example.image_analyzer.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping(value="/analyze",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> analyze(@RequestPart("file") MultipartFile file){
        ImageRecord imageRecord = imageService.submitForAnalysis(file);
        return new ResponseEntity<>(imageRecord, HttpStatus.OK);
    }
}


