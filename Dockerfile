# Stage 1: build with Maven
FROM maven:3.9.0-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom + wrapper, download deps (cacheable)
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: runtime
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the fat JAR from the builder stage into /app/app.jar
COPY --from=builder /app/target/*.jar ./app.jar


# Tell Java to run the JAR in the current directory
ENTRYPOINT ["java","-jar","app.jar"]
