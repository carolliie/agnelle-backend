package com.agnelle.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StorageService {

    @Value("${upload.path}")
    private String uploadPath;

    public String storeImage(MultipartFile file) throws IOException {
        File dir = new File(uploadPath);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(uploadPath, fileName);

        if (Files.exists(filePath)) {
            throw new IOException("Este arquivo já existe. " + fileName);
        }

        Files.copy(file.getInputStream(), filePath);

        return "http://localhost:8080/api/images/" + fileName;
    }

}
