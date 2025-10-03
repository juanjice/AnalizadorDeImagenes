package com.example.image_analyzer.service;

import com.example.image_analyzer.entity.ImageRecord;
import com.example.image_analyzer.repository.ImageRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

    @Autowired
    private ImageRecordRepository imageRecordRepository;

    public ImageRecord submitForAnalysis(MultipartFile file){
        return null;
    }

}
