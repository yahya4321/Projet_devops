# Use an official OpenJDK runtime as the base image
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build directory to the container
COPY target/tp-foyer-5.5.0.jar app.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
