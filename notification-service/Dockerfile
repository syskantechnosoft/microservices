FROM maven:3.9.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY . .
RUN mvn -pl notification-service -am -DskipTests package

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/notification-service/target/notification-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
