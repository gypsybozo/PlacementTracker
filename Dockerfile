# Build stage
FROM eclipse-temurin:17 AS build
WORKDIR /app

# Copy the wrapper and permissions
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Copy the rest of the code and build
COPY src ./src
COPY application.properties ./application.properties
RUN ./mvnw clean install -DskipTests

# Run stage
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=build /app/target/coding-tracker-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
