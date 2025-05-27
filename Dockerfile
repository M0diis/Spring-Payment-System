FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY build/libs/payment-service*.jar app.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]