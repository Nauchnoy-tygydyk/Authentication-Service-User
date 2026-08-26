FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/*.jar service.jar
ENTRYPOINT ["java", "-jar", "service.jar"]