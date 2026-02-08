# 실행 환경 (최종 실행 이미지만 남김)
FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY build/libs/*.jar /app/moddo.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "moddo.jar"]