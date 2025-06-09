# 실행 환경 (최종 실행 이미지만 남김)
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/moddo.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "moddo.jar"]