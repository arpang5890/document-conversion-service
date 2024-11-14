# Document Conversion Microservice

The **Document Conversion Microservice** is designed to handle document conversions efficiently, allowing seamless transitions from one format to another through a set of APIs.

## High level details

### Endpoints
- Submit Document for Conversion (Async)
- Get Conversion Status
- Download Converted Document

### Features
- Asynchronous document submission for conversion.
- Conversion status checks.
- Integrated with RabbitMQ.
- In-memory caching for enhanced performance. (Around Get conversion status API)
- API rate limiting to ensure fair usage.  (Around Submit a document for conversion API)
- Using Instrument Metrics to capture the document conversion request metrics (total request, success, failed, time taken etc.)

### Technologies Used
- **Java**: Version 21
- **Spring Boot**: Framework for building microservices.
- **H2 Database**: in-memory database for temporary storage.
- **RabbitMQ**: Message broker for handling asynchronous tasks.
- **Bucket4J**: Library for API rate limiting (State management - In-memory).
- **Spring Cache**: Used for caching status checks (In-memory).

## Developer's guide

## Documentation

- [Developer's guide][]
- [CICD Guide][]

[Developer's guide]: ./docs/dev_guide.md
[CICD Guide]: ./docs/cicd_guide.md
