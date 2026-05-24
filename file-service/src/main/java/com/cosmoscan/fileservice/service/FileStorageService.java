package com.cosmoscan.fileservice.service;

import com.cosmoscan.common.dto.FileUploadResponse;
import com.cosmoscan.fileservice.entity.FileMetadata;
import com.cosmoscan.fileservice.exception.FileNotFoundException;
import com.cosmoscan.fileservice.exception.FileStorageException;
import com.cosmoscan.fileservice.repository.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final FileMetadataRepository metadataRepository;

    @Value("${app.storage.location:./data/files}")
    private String storageLocation;

    public FileUploadResponse storeFile(MultipartFile file) {
        String fileId = UUID.randomUUID().toString();

        try {
            Path uploadPath = Paths.get(storageLocation);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path targetPath = uploadPath.resolve(fileId);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            FileMetadata metadata = FileMetadata.builder()
                    .id(fileId)
                    .originalFilename(file.getOriginalFilename())
                    .sizeBytes(file.getSize())
                    .contentType(file.getContentType())
                    .storagePath(targetPath.toString())
                    .build();

            metadataRepository.save(metadata);

            log.info("File stored successfully: {} (ID: {})", file.getOriginalFilename(), fileId);

            return FileUploadResponse.builder()
                    .fileId(fileId)
                    .originalFilename(file.getOriginalFilename())
                    .sizeBytes(file.getSize())
                    .contentType(file.getContentType())
                    .uploadedAt(metadata.getUploadedAt())
                    .build();

        } catch (IOException e) {
            log.error("Failed to store file: {}", file.getOriginalFilename(), e);
            throw new FileStorageException("Failed to store file: " + file.getOriginalFilename(), e);
        }
    }

    public FileData loadFile(String fileId) {
        FileMetadata metadata = metadataRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found: " + fileId));

        Path filePath = Paths.get(metadata.getStoragePath());
        Resource resource = new FileSystemResource(filePath);

        if (!resource.exists()) {
            throw new FileNotFoundException("File not found on disk: " + fileId);
        }

        return new FileData(resource, metadata.getOriginalFilename(), metadata.getContentType());
    }

    public FileUploadResponse getFileMetadata(String fileId) {
        FileMetadata metadata = metadataRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found: " + fileId));

        return FileUploadResponse.builder()
                .fileId(metadata.getId())
                .originalFilename(metadata.getOriginalFilename())
                .sizeBytes(metadata.getSizeBytes())
                .contentType(metadata.getContentType())
                .uploadedAt(metadata.getUploadedAt())
                .build();
    }

    public record FileData(Resource resource, String originalFilename, String contentType) {}
}