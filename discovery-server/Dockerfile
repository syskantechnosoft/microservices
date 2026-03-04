FROM maven:3.9.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY . .
RUN mvn -pl discovery-server -am -DskipTests package

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/discovery-server/target/discovery-server-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
