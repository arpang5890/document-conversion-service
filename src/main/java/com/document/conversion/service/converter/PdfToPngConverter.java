package com.document.conversion.service.converter;

import com.document.conversion.model.Document;
import com.document.conversion.service.FileStorageService;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PdfToPngConverter implements DocumentConverter {

    private final FileStorageService fileStorageService;

    @Override
    public String convert(Document document) throws IOException {
        try (InputStream inputStream = Files.newInputStream(fileStorageService.getFilePath(document.getOriginalFilePath()));
             PDDocument pdf = PDDocument.load(inputStream)) {
            PDFRenderer renderer = new PDFRenderer(pdf);
            BufferedImage image = renderer.renderImageWithDPI(0, 300); // Convert first page at 300 DPI
            String convertedFileName = "converted-" + UUID.randomUUID() + ".png";
            File outputFile = fileStorageService.getFilePath(convertedFileName).toFile();
            ImageIO.write(image, "PNG", outputFile);
            return convertedFileName;
        }
    }
}