package com.document.conversion.service.converter;

import com.document.conversion.model.Document;

import java.io.IOException;

public interface DocumentConverter {
    String convert(Document document) throws IOException;
}
