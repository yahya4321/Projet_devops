FROM openjdk:17-jdk-alpine
ARG JAR_FILE
WORKDIR /app
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
