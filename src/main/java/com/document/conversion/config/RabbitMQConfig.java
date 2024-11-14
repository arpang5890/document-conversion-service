package com.document.conversion.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_DOCUMENT_CONVERSION = "document-conversion-queue";
    public static final String EXCHANGE_DOCUMENT_CONVERSION = "document-conversion-exchange";
    public static final String ROUTING_KEY_DOCUMENT_CONVERSION = "document.conversion";

    @Bean
    public Queue documentConversionQueue() {
        return QueueBuilder.durable(QUEUE_DOCUMENT_CONVERSION)
                .deadLetterExchange(EXCHANGE_DOCUMENT_CONVERSION + ".dlx")
                .deadLetterRoutingKey(ROUTING_KEY_DOCUMENT_CONVERSION + ".dlq")
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(QUEUE_DOCUMENT_CONVERSION + ".dlq").build();
    }

    @Bean
    public DirectExchange documentConversionExchange() {
        return new DirectExchange(EXCHANGE_DOCUMENT_CONVERSION);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(EXCHANGE_DOCUMENT_CONVERSION + ".dlx");
    }

    @Bean
    public Binding bindingDocumentConversion() {
        return BindingBuilder.bind(documentConversionQueue())
                .to(documentConversionExchange())
                .with(ROUTING_KEY_DOCUMENT_CONVERSION);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(ROUTING_KEY_DOCUMENT_CONVERSION + ".dlq");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
