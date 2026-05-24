FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S cosmoscan && adduser -S cosmoscan -G cosmoscan
RUN mkdir -p /data/wordclouds /data/reports && chown -R cosmoscan:cosmoscan /data
USER cosmoscan
COPY build/libs/analysis-service.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 8082