FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/*.jar

ENV SPRING_PROFILES_ACTIVE=prod
COPY ${JAR_FILE} moddo.jar
ENTRYPOINT ["java","-jar","/moddo.jar"]

RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime