package com.document.conversion.service;

import com.document.conversion.exception.ConversionException;
import com.document.conversion.exception.DocumentNotFoundException;
import com.document.conversion.model.Document;
import com.document.conversion.repository.DocumentRepository;

import com.document.conversion.service.converter.DocumentConvertFactory;
import com.document.conversion.service.converter.DocumentConverter;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentConversionService {

  private final DocumentRepository documentRepository;
  private final MetricsService metricsService;
  private final DocumentConvertFactory documentConvertFactory;

  @Transactional
  public void startConversion(UUID documentId) {
    Document document = documentRepository.findById(documentId).orElse(null);
    if (document == null) {
      log.warn("document: {} not found", documentId);
      return;
    }

    Timer.Sample sample = null;
    try {
      sample = metricsService.startTimer();
      metricsService.recordConversionStart();
      document.setStatus(Document.ConversionStatus.IN_PROGRESS);
      documentRepository.saveAndFlush(document);

      String convertedFilePath = performConversion(document);
      document.setConvertedFilePath(convertedFilePath);
      document.setStatus(Document.ConversionStatus.COMPLETED);
      metricsService.recordConversionSuccess();
    } catch (Exception e) {
      log.error("Conversion failed for document: {}", documentId, e);
      document.setStatus(Document.ConversionStatus.FAILED);
      document.setErrorMessage(e.getMessage());
      metricsService.recordConversionFailure();
    } finally {
      if (sample != null) {
        metricsService.stopTimer(sample);
      }
    }
    documentRepository.save(document);
  }

  private String performConversion(Document document) throws IOException {
    SupportedSourceFormat sourceFormat = getSourceFormat(document.getOriginalFormat());
    SupportedTargetFormat targetFormat = getTargetFormat(document.getTargetFormat());
    DocumentConverter documentConverter =
        documentConvertFactory.getConverter(sourceFormat, targetFormat);
    return documentConverter.convert(document);
  }

  private SupportedSourceFormat getSourceFormat(String format) {
    try {
      return SupportedSourceFormat.valueOf(format.toLowerCase());
    } catch (IllegalArgumentException e) {
      throw new ConversionException("Unsupported source format: " + format);
    }
  }

  private SupportedTargetFormat getTargetFormat(String format) {
    try {
      return SupportedTargetFormat.valueOf(format.toLowerCase());
    } catch (IllegalArgumentException e) {
      throw new ConversionException("Unsupported target format: " + format);
    }
  }

  public enum SupportedSourceFormat {
    pdf
  }

  public enum SupportedTargetFormat {
    png,
    word
  }
}
