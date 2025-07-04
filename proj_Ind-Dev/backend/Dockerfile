FROM openjdk:24-jdk-oracle

WORKDIR /app

COPY target/book-exchange-backend-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]