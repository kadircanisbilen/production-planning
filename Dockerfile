FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/production-planning-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
