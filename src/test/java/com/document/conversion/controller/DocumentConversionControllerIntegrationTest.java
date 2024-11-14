package com.document.conversion.controller;

import com.document.conversion.model.ConversionResponse;
import com.document.conversion.model.Document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DocumentConversionControllerIntegrationTest extends BaseIT {

    @Test
    public void submitConversion_shouldAcceptAndReturnResponse() throws IOException {
        ResponseEntity<ConversionResponse> responseEntity = submitConversionRequest("png");
        validateResponseStatus(responseEntity, HttpStatus.ACCEPTED);
        validateConversionStatus(Objects.requireNonNull(responseEntity.getBody()), Document.ConversionStatus.PENDING.name());
    }

    @Test
    public void getStatus_shouldReturn404() {
        String url = String.format("%s%s/api/v1/documents/%s/status", BASE_URL, port, UUID.randomUUID());
        HttpClientErrorException exception = Assertions.assertThrows(
                HttpClientErrorException.class,
                () -> restTemplate.getForEntity(url, Map.class)
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void getStatus_shouldReturnCompletedStatus() throws InterruptedException, IOException {
        ResponseEntity<ConversionResponse> responseEntity = submitConversionRequest("png");
        Thread.sleep(3000);
        ResponseEntity<ConversionResponse> statusResponse = getDocumentStatus(Objects.requireNonNull(responseEntity.getBody()).getDocumentId());
        validateResponseStatus(statusResponse, HttpStatus.OK);
        validateConversionStatus(Objects.requireNonNull(statusResponse.getBody()), Document.ConversionStatus.COMPLETED.name());
    }

    @Test
    public void getStatus_shouldReturnFailedStatus() throws InterruptedException, IOException {
        ResponseEntity<ConversionResponse> responseEntity = submitConversionRequest("txt");
        Thread.sleep(3000);
        ResponseEntity<ConversionResponse> statusResponse = getDocumentStatus(Objects.requireNonNull(responseEntity.getBody()).getDocumentId());
        validateResponseStatus(statusResponse, HttpStatus.OK);
        validateConversionStatus(Objects.requireNonNull(statusResponse.getBody()), Document.ConversionStatus.FAILED.name());
    }

    @Test
    public void downloadDocument_shouldReturnFile() throws InterruptedException, IOException {
        ResponseEntity<ConversionResponse> conversionResponse = submitConversionRequest("png");
        Thread.sleep(3000);
        ResponseEntity<byte[]> downloadResponse = downloadDocument(Objects.requireNonNull(conversionResponse.getBody()).getDocumentId());
        validateDownloadResponse(downloadResponse);
    }

    private ResponseEntity<ConversionResponse> submitConversionRequest(String targetFormat) throws IOException {
        String url = BASE_URL + port + "/api/v1/documents/convert";
        HttpEntity<MultiValueMap<String, Object>> requestEntity = createMultipartRequest(targetFormat);
        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, ConversionResponse.class);
    }

    private ResponseEntity<ConversionResponse> getDocumentStatus(UUID documentId) {
        String url = String.format("%s%s/api/v1/documents/%s/status", BASE_URL, port, documentId);
        return restTemplate.getForEntity(url, ConversionResponse.class);
    }

    private ResponseEntity<byte[]> downloadDocument(UUID documentId) {
        String url = BASE_URL + port + "/api/v1/documents/" + documentId + "/download";
        return restTemplate.getForEntity(url, byte[].class);
    }

    private HttpEntity<MultiValueMap<String, Object>> createMultipartRequest(String targetFormat) throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(createPDFContent()) {
            @Override
            public String getFilename() {
                return "test.pdf";
            }
        });
        body.add("targetFormat", targetFormat);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return new HttpEntity<>(body, headers);
    }

    private byte[] createPDFContent() throws IOException {
        // Create a new PDF document
        try (PDDocument document = new PDDocument()) {
            // Create a new page and add it to the document
            PDPage page = new PDPage();
            document.addPage(page);
            // Prepare content stream for writing text
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Hello, this is a test PDF created using PDFBox!");
                contentStream.endText();
            }
            // Write content to a byte array output stream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }


    private void validateResponseStatus(ResponseEntity<?> response, HttpStatus expectedStatus) {
        assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
        assertThat(response.getBody()).isNotNull();
    }

    private void validateConversionStatus(ConversionResponse response, String expectedStatus) {
        assertThat(response.getStatus()).isEqualTo(expectedStatus);
    }

    private void validateDownloadResponse(ResponseEntity<byte[]> response) {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_OCTET_STREAM);
        assertThat(response.getHeaders().getContentDisposition().getFilename()).isEqualTo("converted-document");
        assertThat(response.getBody()).isNotEmpty();
    }
}
