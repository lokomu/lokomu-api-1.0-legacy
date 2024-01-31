### STAGE 1: Build ###
FROM maven:3-openjdk-21-slim AS build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD pom.xml $HOME
RUN mvn verify --fail-never
ADD . $HOME
RUN mvn clean package

### STAGE 2: Run ###
FROM openjdk:21-alpine
COPY --from=build /usr/app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]