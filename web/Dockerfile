# ---- Build the JAR first ----
# Run from the project root:
#   ./build-web.sh
#
# Then build the Docker image:
#   docker build -t oxygen-batch-converter-web web/

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY target/app.jar /app/app.jar

ENV PORT=8080
EXPOSE 8080

CMD ["java","-XX:MaxRAMPercentage=75","-jar","/app/app.jar"]
