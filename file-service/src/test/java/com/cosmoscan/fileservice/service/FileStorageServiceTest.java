package com.cosmoscan.fileservice.service;

import com.cosmoscan.common.dto.FileUploadResponse;
import com.cosmoscan.fileservice.entity.FileMetadata;
import com.cosmoscan.fileservice.exception.FileNotFoundException;
import com.cosmoscan.fileservice.repository.FileMetadataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    @Mock
    private FileMetadataRepository metadataRepository;

    @InjectMocks
    private FileStorageService fileStorageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileStorageService, "storageLocation", tempDir.toString());
    }

    @Test
    void shouldStoreFileSuccessfully() {
        // given
        MultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Hello World".getBytes()
        );

        when(metadataRepository.save(any(FileMetadata.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        FileUploadResponse response = fileStorageService.storeFile(file);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getFileId()).isNotNull();
        assertThat(response.getOriginalFilename()).isEqualTo("test.txt");
        assertThat(response.getSizeBytes()).isEqualTo(11);
        verify(metadataRepository, times(1)).save(any(FileMetadata.class));
    }

    @Test
    void shouldLoadFileSuccessfully() throws IOException {
        // given
        String fileId = "test-123";
        FileMetadata metadata = FileMetadata.builder()
                .id(fileId)
                .originalFilename("test.txt")
                .sizeBytes(11L)
                .contentType("text/plain")
                .storagePath(tempDir.resolve(fileId).toString())
                .build();

        when(metadataRepository.findById(fileId)).thenReturn(Optional.of(metadata));

        // Создаем файл на диске
        tempDir.resolve(fileId).toFile().createNewFile();

        // when
        var fileData = fileStorageService.loadFile(fileId);

        // then
        assertThat(fileData).isNotNull();
        assertThat(fileData.originalFilename()).isEqualTo("test.txt");
    }

    @Test
    void shouldThrowExceptionWhenFileNotFound() {
        // given
        String fileId = "non-existent";
        when(metadataRepository.findById(fileId)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> fileStorageService.loadFile(fileId))
                .isInstanceOf(FileNotFoundException.class)
                .hasMessageContaining("File not found");
    }
}