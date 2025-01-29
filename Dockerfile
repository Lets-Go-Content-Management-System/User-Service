## Use OpenJDK as the base image
#FROM openjdk:17-jdk-slim
#
#
## Set the working directory in the container
#WORKDIR /app
#
## Copy the JAR file into the container (replace `your-app.jar` with your actual JAR filename)
#COPY target/user-service-0.0.1-SNAPSHOT.jar user-service.jar
#
## Expose port 8081 (same as in docker-compose)
#EXPOSE 8081
#
## Run the application
#CMD ["java", "-jar", "user-microservice.jar"]
