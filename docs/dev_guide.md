## Developer's guide

### Prerequisites:

- java 21
- Maven 3.x
- Docker (testing)

### Build and test

```shell
mvn clean verify
```

### What's used

#### Lombok

[Lombok](https://projectlombok.org/). To view the source code in your IDE, you need to install a
plugin that recognizes the Lombok annotations. To configure your IDE, follow the appropriate setup
link found
[here](https://projectlombok.org/setup/overview).

### Running locally

#### Run Rabbitmq

You can run Rabbitmq in any convenient way, we also provide with docker-compose file with rabbitmq
container setup.
Run rabbitmq locally via docker compose. Go to {projectDirectory}/docker

```bash
docker-compose up
```

#### Run Document Conversion application

Either use intellij runner or use maven command (from project root):

```bash
mvn spring-boot:run
``` 

### Integration tests

By default, the basic maven build runs all IT-s.
If, for some reason, run the build without tests

```bash
mvn clean verify -DskipTests
```

### Verify the application locally
- Health http://localhost:8080/api/v1/actuator/health

### API Docs (Swagger):
- Swagger http://localhost:8080/api/v1/swagger-ui/index.html