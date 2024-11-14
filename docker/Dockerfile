# Use Eclipse Temurin base image for Java 21
FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY target/document-conversion-service.jar document-conversion-service.jar

# JVM tuning parameters for optimal performance
ENV JAVA_OPTS="-XX:+UseG1GC \
               -XX:+ParallelRefProcEnabled \
               -XX:MaxInlineSize=150 \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+AlwaysPreTouch \
               -Dfile.encoding=UTF-8 \
               -Djava.security.egd=file:/dev/./urandom \
               -XX:+HeapDumpOnOutOfMemoryError \
               -XX:HeapDumpPath=/app/heap-dumps/"

# Create a directory for heap dumps
RUN mkdir -p /app/heap-dumps

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar document-conversion-service.jar"]