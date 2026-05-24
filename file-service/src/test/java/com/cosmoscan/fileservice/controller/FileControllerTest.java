package com.cosmoscan.fileservice.controller;

import com.cosmoscan.common.dto.FileUploadResponse;
import com.cosmoscan.fileservice.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileStorageService fileStorageService;

    @Test
    void shouldUploadFile() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "content".getBytes()
        );

        FileUploadResponse response = FileUploadResponse.builder()
                .fileId("test-123")
                .originalFilename("test.txt")
                .sizeBytes(7L)
                .contentType("text/plain")
                .uploadedAt(LocalDateTime.now())
                .build();

        when(fileStorageService.storeFile(any())).thenReturn(response);

        // when/then
        mockMvc.perform(multipart("/api/v1/files").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.file_id").value("test-123"))
                .andExpect(jsonPath("$.original_filename").value("test.txt"));
    }

    @Test
    void shouldReturnFileMetadata() throws Exception {
        // given
        String fileId = "test-123";
        FileUploadResponse response = FileUploadResponse.builder()
                .fileId(fileId)
                .originalFilename("test.txt")
                .sizeBytes(100L)
                .build();

        when(fileStorageService.getFileMetadata(fileId)).thenReturn(response);

        // when/then
        mockMvc.perform(get("/api/v1/files/{fileId}/metadata", fileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.file_id").value(fileId));
    }
}