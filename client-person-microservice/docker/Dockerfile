FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/client-person-docker.jar /app/client-person-docker.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/client-person-docker.jar"]
