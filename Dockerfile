FROM openjdk:11-jdk-slim
ARG JAR_FILE=build/libs/myMoments-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} myMoments.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","myMoments.jar"]