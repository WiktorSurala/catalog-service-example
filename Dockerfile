FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/catalog-service-example-0.0.1-SNAPSHOT.jar catalog-service.jar
ENTRYPOINT ["java", "-jar", "catalog-service.jar"]
