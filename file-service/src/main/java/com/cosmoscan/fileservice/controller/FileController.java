package com.cosmoscan.fileservice.controller;

import com.cosmoscan.common.dto.FileUploadResponse;
import com.cosmoscan.fileservice.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file) {

        log.info("Received file upload request: {}", file.getOriginalFilename());
        FileUploadResponse response = fileStorageService.storeFile(file);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        log.info("Received download request for file: {}", fileId);

        var fileData = fileStorageService.loadFile(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        fileData.contentType() != null ? fileData.contentType() : "application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileData.originalFilename() + "\"")
                .body(fileData.resource());
    }

    @GetMapping("/{fileId}/metadata")
    public ResponseEntity<FileUploadResponse> getFileMetadata(@PathVariable String fileId) {
        log.info("Received metadata request for file: {}", fileId);
        return ResponseEntity.ok(fileStorageService.getFileMetadata(fileId));
    }
}