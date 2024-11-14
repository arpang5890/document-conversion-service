package com.document.conversion.controller;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BaseIT {

    @LocalServerPort
    protected int port;
    protected static final String BASE_URL = "http://localhost:";
    protected static RestTemplate restTemplate;

    @Container
    private static final GenericContainer<?> rabbitMQContainer = new GenericContainer<>("rabbitmq:management")
            .withExposedPorts(5672, 15672);

    @BeforeAll
    public static void setUp() {
        rabbitMQContainer.start();
        restTemplate = new RestTemplate();
    }
}
