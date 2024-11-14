package com.document.conversion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DocumentConversionApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DocumentConversionApplication.class, args);
    }
}
