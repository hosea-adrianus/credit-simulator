# --- Build stage: compile, test and package the fat jar ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Download dependencies first so they are cached until pom.xml changes.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package

# --- Runtime stage: JRE only ---
FROM eclipse-temurin:17-jre
WORKDIR /app

RUN useradd --system --no-create-home simulator
COPY --from=build /build/target/credit-simulator.jar /app/credit-simulator.jar
USER simulator

# Interactive:  docker run -it <image>
# File input:   docker run -i -v "$PWD/file_inputs.txt:/data/file_inputs.txt:ro" <image> /data/file_inputs.txt
ENTRYPOINT ["java", "-jar", "/app/credit-simulator.jar"]
