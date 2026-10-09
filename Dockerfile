FROM maven:3.9.9-eclipse-temurin-22 AS build
WORKDIR /app
COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline
COPY src ./src
RUN mvn -B -ntp package -DskipTests

FROM eclipse-temurin:22-jre
WORKDIR /app
COPY --from=build /app/target/Murza-0.0.1-SNAPSHOT.jar /app/murza-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/murza-app.jar"]
