# Stage 1: Build the application
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy build files
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY gradlew ./

# Make gradlew executable
RUN chmod +x ./gradlew

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src ./src

# Build the JAR
RUN ./gradlew build --no-daemon -x test

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/*_fat.jar ./pmu-be.jar

# Create a non-root user
RUN useradd -m myuser
USER myuser

EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "pmu-be.jar"]
