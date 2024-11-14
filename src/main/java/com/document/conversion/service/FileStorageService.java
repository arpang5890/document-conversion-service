package com.document.conversion.service;

import com.document.conversion.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${app.document.storage.location}") String fileStorageLocation) {
        this.fileStorageLocation = initializeStorageLocation(fileStorageLocation);
    }

    private Path initializeStorageLocation(String storagePath) {
        Path path = Paths.get(storagePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(path);
            return path;
        } catch (IOException ex) {
            String errorMessage = "Could not create the directory where the uploaded files will be stored.";
            log.error(errorMessage, ex);
            throw new FileStorageException(errorMessage, ex);
        }
    }

    public String storeFile(MultipartFile file, String prefix) {
        String fileName = buildFileName(file, prefix);
        validateFileName(fileName);
        return copyFileToStorage(file, fileName);
    }

    public Path getFilePath(String fileName) {
        return fileStorageLocation.resolve(fileName);
    }

    public byte[] readFile(String fileName) {
        try {
            Path filePath = getFilePath(fileName);
            return Files.readAllBytes(filePath);
        } catch (IOException ex) {
            String errorMessage = "Could not read file " + fileName;
            log.error(errorMessage, ex);
            throw new FileStorageException(errorMessage, ex);
        }
    }

    private String buildFileName(MultipartFile file, String prefix) {
        return StringUtils.cleanPath(prefix + "-" + UUID.randomUUID() + "-" + file.getOriginalFilename());
    }

    private void validateFileName(String fileName) {
        if (fileName.contains("..")) {
            String errorMessage = "Filename contains invalid path sequence: " + fileName;
            log.warn(errorMessage);
            throw new FileStorageException(errorMessage);
        }
    }

    private String copyFileToStorage(MultipartFile file, String fileName) {
        try {
            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("File stored successfully: {}", fileName);
            return fileName;
        } catch (IOException ex) {
            String errorMessage = "Could not store file " + fileName;
            log.error(errorMessage, ex);
            throw new FileStorageException(errorMessage, ex);
        }
    }

}
