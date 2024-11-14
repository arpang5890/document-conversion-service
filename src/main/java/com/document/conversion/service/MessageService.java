package com.document.conversion.service;

import com.document.conversion.config.RabbitMQConfig;
import com.document.conversion.model.ConversionMessage;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageService {

    private final RabbitTemplate rabbitTemplate;
    private final DocumentConversionService documentConversionService;

    public void publishDocumentConversionRequest(UUID documentId) {
        ConversionMessage message = new ConversionMessage(documentId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_DOCUMENT_CONVERSION,
                RabbitMQConfig.ROUTING_KEY_DOCUMENT_CONVERSION,
                message
        );
        log.info("Sent conversion message for document: {}", documentId);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_DOCUMENT_CONVERSION)
    public void handleDocumentConversionRequest(ConversionMessage message) {
        log.info("Received conversion message for document: {}", message.getDocumentId());
        try {
            documentConversionService.startConversion(message.getDocumentId());
        } catch (Exception e) {
            log.error("Error processing conversion message for document: {}",
                    message.getDocumentId(), e);
            throw e; // Will be sent to DLQ
        }
    }
}
