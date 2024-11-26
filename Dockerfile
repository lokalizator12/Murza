#FROM openjdk:22
#WORKDIR /app
#COPY target/Murza-0.0.1-SNAPSHOT.jar /app/murza-app.jar
#RUN apt-get update && apt-get install -y maven
#RUN mvn clean package -DskipTests
#EXPOSE 8080
#
#ENTRYPOINT ["java", "-jar", "murza-app.jar"]