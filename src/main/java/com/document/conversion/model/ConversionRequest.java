package com.document.conversion.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ConversionRequest {
    @NotNull(message = "File is required")
    private MultipartFile file;

    @NotBlank(message = "Target format is required")
    private String targetFormat;
}
