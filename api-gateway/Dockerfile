FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S cosmoscan && adduser -S cosmoscan -G cosmoscan
USER cosmoscan
COPY build/libs/api-gateway.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 8080