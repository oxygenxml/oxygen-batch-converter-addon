# ---- build stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package

# ---- run stage ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/target/app.jar /app/app.jar

ENV PORT=8080
EXPOSE 8080

CMD ["java","-XX:MaxRAMPercentage=75","-jar","/app/app.jar"]
