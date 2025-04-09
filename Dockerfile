# Use an official Java 17 image as the base
FROM eclipse-temurin:17

# Set the working directory
WORKDIR /app

# Copy Maven wrapper files and pom.xml
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn

# Download dependencies
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Copy the source code
COPY src ./src

# 🔥 Copy the .env file into the container
COPY .env .env

# Build the application
RUN ./mvnw clean install -DskipTests

# Run the application
CMD ["java", "-jar", "target/coding-tracker-0.0.1-SNAPSHOT.jar"]
