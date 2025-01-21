FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} moddo.jar
ENTRYPOINT ["java","-jar","/moddo.jar"]

RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime