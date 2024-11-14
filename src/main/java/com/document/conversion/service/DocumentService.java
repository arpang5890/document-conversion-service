package com.document.conversion.service;

import com.document.conversion.exception.ConversionException;
import com.document.conversion.exception.DocumentNotFoundException;
import com.document.conversion.model.ConversionRequest;
import com.document.conversion.model.ConversionResponse;
import com.document.conversion.model.Document;
import com.document.conversion.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final FileStorageService fileStorageService;
    private final MessageService messageService;

    @Transactional
    public ConversionResponse submitConversion(ConversionRequest request) {
        MultipartFile file = request.getFile();
        String storedFileName = fileStorageService.storeFile(file, "original");
        Document dbDocument = documentRepository.save(buildDocument(request, storedFileName, file));
        messageService.publishDocumentConversionRequest(dbDocument.getId());
        return buildConversionResponse(dbDocument);
    }

    @Cacheable(value = "documentStatusManager", key = "#documentId")
    public ConversionResponse getStatus(UUID documentId) {
        Document dbDocument = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found"));
        return buildConversionResponse(dbDocument);
    }

    public byte[] getConvertedDocument(UUID documentId) {
        Document dbDocument = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found"));
        if (dbDocument.getStatus() != Document.ConversionStatus.COMPLETED) {
            throw new ConversionException("Document conversion not completed");
        }
        return fileStorageService.readFile(dbDocument.getConvertedFilePath());
    }

    private Optional<String> getFileExtension(String fileName) {
        return Optional.ofNullable(fileName)
                .filter(name -> name.contains("."))
                .map(name -> name.substring(name.lastIndexOf(".") + 1).toLowerCase());
    }

    private ConversionResponse buildConversionResponse(Document document) {
        return ConversionResponse.builder()
                .documentId(document.getId())
                .errorMessage(document.getErrorMessage())
                .status(document.getStatus().name())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

    private Document buildDocument(ConversionRequest request, String storedFileName, MultipartFile file) {
        String originalFormat = getFileExtension(file.getOriginalFilename())
                .orElseThrow(() -> new IllegalArgumentException("Invalid file format"));
        return Document.builder()
                .originalFileName(file.getOriginalFilename())
                .originalFormat(originalFormat)
                .targetFormat(request.getTargetFormat())
                .status(Document.ConversionStatus.PENDING)
                .originalFilePath(storedFileName)
                .build();
    }
}
