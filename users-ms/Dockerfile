FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copiar proyecto
COPY . .

# Instalar Maven
RUN apt-get update && apt-get install -y maven

# Construir JAR
RUN mvn -q -DskipTests package

# ====== Runtime ======
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copiar JAR build
COPY --from=build /app/target/users-ms-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
