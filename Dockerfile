FROM eclipse-temurin:17-jdk
ARG JAR_FILE=target/*.jar
COPY ./target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

