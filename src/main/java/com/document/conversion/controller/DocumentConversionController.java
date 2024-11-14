package com.document.conversion.controller;

import com.document.conversion.annotation.RateLimit;
import com.document.conversion.model.ConversionRequest;
import com.document.conversion.model.ConversionResponse;
import com.document.conversion.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@Tag(name = "Document Conversion API", description = "APIs for document conversion operations")
public class DocumentConversionController {

    private final DocumentService documentService;

    @Operation(
            summary = "Submit document for conversion",
            description = "Upload a document and initiate the conversion process to the specified format"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Conversion request accepted"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    @PostMapping(value = "/convert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RateLimit
    public ResponseEntity<ConversionResponse> submitConversion(@Valid @ModelAttribute ConversionRequest request) {
        ConversionResponse response = documentService.submitConversion(request);
        return ResponseEntity.accepted().body(response);
    }

    @Operation(
            summary = "Get conversion status",
            description = "Check the current status of a document conversion request"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Document not found")
    })
    @GetMapping("/{documentId}/status")
    public ResponseEntity<ConversionResponse> getStatus(
            @Parameter(description = "Document ID", required = true)
            @PathVariable UUID documentId) {
        return ResponseEntity.ok(documentService.getStatus(documentId));
    }

    @Operation(
            summary = "Download converted document",
            description = "Download the converted document if the conversion is complete"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Document downloaded successfully"),
            @ApiResponse(responseCode = "404", description = "Document not found"),
            @ApiResponse(responseCode = "409", description = "Conversion not yet complete")
    })
    @GetMapping("/{documentId}/download")
    public ResponseEntity<byte[]> downloadDocument(
            @Parameter(description = "Document ID", required = true)
            @PathVariable UUID documentId) {
        byte[] document = documentService.getConvertedDocument(documentId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename("converted-document").build());
        return new ResponseEntity<>(document, headers, HttpStatus.OK);
    }
}
