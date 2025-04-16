package com.agnelle.backend.controller;

import com.agnelle.backend.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/images")
@CrossOrigin(origins = "*")
public class ImageController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = storageService.storeImage(file);
            return ResponseEntity.ok("Image uploaded successfully: " + imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<?> getImage(@PathVariable String fileName) {
        try {
            Path path = Paths.get("uploads/" + fileName);
            Resource resource = new FileSystemResource(path.toFile());

            String fileExtension = getFileExtension(fileName);

            MediaType mediaType = getMediaTypeByExtension(fileExtension);

            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok().contentType(mediaType).body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to load image.");
        }
    }

    @GetMapping
    public ResponseEntity<?> getImages() {
        try {
            Path uploadDirectory = Paths.get("uploads");

            if (Files.notExists(uploadDirectory)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            List<String> imagesUrls = Files.walk(uploadDirectory)
                    .filter(Files::isRegularFile)
                    .map(path -> "http://localhost:8080/api/images/" + path.getFileName().toString())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(imagesUrls);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to load images.");
        }
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<?> deleteImage(@PathVariable String fileName) {
        try {
            Path path = Paths.get("uploads/" + fileName);

            if (Files.notExists(path)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found.");
            }
            Files.delete(path);
            return ResponseEntity.ok("Image deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete image.");
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex == -1 ? "" : fileName.substring(dotIndex + 1);
    }

    private MediaType getMediaTypeByExtension(String fileExtension) {
        switch (fileExtension) {
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            case "png":
                return MediaType.IMAGE_PNG;
            case "gif":
                return MediaType.IMAGE_GIF;
            case "webp":
                return MediaType.valueOf("image/webp");
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
