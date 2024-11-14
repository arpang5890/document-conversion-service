package com.document.conversion.service.converter;

import com.document.conversion.model.Document;
import com.document.conversion.service.FileStorageService;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PdfToWordConverter implements DocumentConverter {

    private final FileStorageService fileStorageService;

    @Override
    public String convert(Document document) throws IOException {
        try (InputStream inputStream = Files.newInputStream(fileStorageService.getFilePath(document.getOriginalFilePath()));
             PDDocument pdf = PDDocument.load(inputStream);
             XWPFDocument doc = new XWPFDocument()) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(pdf);
            String[] lines = text.split("\n");
            for (String line : lines) {
                XWPFParagraph paragraph = doc.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(line);
            }
            String convertedFileName = "converted-" + UUID.randomUUID() + ".docx";
            File outputFile = fileStorageService.getFilePath(convertedFileName).toFile();
            try (FileOutputStream out = new FileOutputStream(outputFile)) {
                doc.write(out);
            }
            return convertedFileName;
        }
    }
}