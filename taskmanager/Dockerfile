FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/taskmanager-0.0.1-SNAPSHOT.jar /app/taskmanager.jar
ENTRYPOINT ["java", "-jar", "taskmanager.jar"]
