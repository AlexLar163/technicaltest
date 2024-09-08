FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/account-movement-docker.jar /app/account-movement-docker.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/account-movement-docker.jar"]
