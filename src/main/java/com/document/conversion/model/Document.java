package com.document.conversion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String originalFileName;
    private String convertedFileName;

    @Column(nullable = false)
    private String originalFormat;
    @Column(nullable = false)
    private String targetFormat;

    private String originalFilePath;
    private String convertedFilePath;

    @Enumerated(EnumType.STRING)
    private ConversionStatus status = ConversionStatus.PENDING;
    private String errorMessage;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum ConversionStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }
}

