FROM maven:3.9-eclipse-temurin-8 AS build
WORKDIR /app
COPY pom.xml maven-settings.xml ./
RUN mvn -s maven-settings.xml -q -Dmaven.test.skip=true dependency:go-offline
COPY src ./src
RUN mvn -s maven-settings.xml -q -Dmaven.test.skip=true package

FROM eclipse-temurin:8-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
