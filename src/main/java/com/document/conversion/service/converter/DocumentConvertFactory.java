package com.document.conversion.service.converter;

import com.document.conversion.exception.ConversionException;
import com.document.conversion.service.DocumentConversionService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DocumentConvertFactory {
    private final Map<String, DocumentConverter> converters;

    public DocumentConvertFactory(PdfToPngConverter pdfToPngConverter, PdfToWordConverter pdfToWordConverter) {
        this.converters = new HashMap<>();
        this.converters.put("PDF_TO_PNG", pdfToPngConverter);
        this.converters.put("PDF_TO_WORD", pdfToWordConverter);
    }

    public DocumentConverter getConverter(DocumentConversionService.SupportedSourceFormat sourceFormat, DocumentConversionService.SupportedTargetFormat targetFormat) {
        String key = sourceFormat.name().toUpperCase() + "_TO_" + targetFormat.name().toUpperCase();
        DocumentConverter converter = converters.get(key);
        if (converter == null) {
            throw new ConversionException("Unsupported conversion format");
        }
        return converter;
    }
}