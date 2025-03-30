# 1단계: 빌드 환경 (Gradle 빌드)
FROM gradle:7.4.2-jdk17 AS builder


WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x ./gradlew
RUN ./gradlew dependencies

COPY src src
RUN ./gradlew bootJar --no-daemon

# 2단계: 실행 환경 (최종 실행 이미지만 남김)
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/moddo.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "moddo.jar"]