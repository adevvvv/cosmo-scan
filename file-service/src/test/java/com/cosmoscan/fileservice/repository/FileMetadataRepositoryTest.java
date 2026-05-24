package com.cosmoscan.fileservice.repository;

import com.cosmoscan.fileservice.entity.FileMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FileMetadataRepositoryTest {

    @Autowired
    private FileMetadataRepository repository;

    @Test
    void shouldSaveAndFindFileMetadata() {
        // given
        FileMetadata metadata = FileMetadata.builder()
                .id("file-1")
                .originalFilename("document.pdf")
                .sizeBytes(2048L)
                .contentType("application/pdf")
                .storagePath("/data/files/file-1")
                .uploadedAt(LocalDateTime.now())
                .build();

        // when
        repository.save(metadata);
        Optional<FileMetadata> found = repository.findById("file-1");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getOriginalFilename()).isEqualTo("document.pdf");
        assertThat(found.get().getSizeBytes()).isEqualTo(2048L);
    }

    @Test
    void shouldReturnEmptyWhenFileNotFound() {
        // when
        Optional<FileMetadata> found = repository.findById("non-existent");

        // then
        assertThat(found).isEmpty();
    }
}