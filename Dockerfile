
## Build stage
##
#FROM maven:3.8.7-eclipse-temurin-17-alpine AS build
#COPY src /home/app/src
#COPY pom.xml /home/app
#RUN mvn -f /home/app/pom.xml clean package -Dmaven.test.skip=true
#
#
##
## Package stage
##
#FROM openjdk:17-slim-buster
#COPY --from=build /home/app/target/PizzeriaProject-0.0.1-SNAPSHOT.jar /usr/local/lib/pizzeriaproject.jar
#EXPOSE 8080
##EXPOSE 80
#ENTRYPOINT ["java","-jar","/usr/local/lib/pizzeriaproject.jar"]