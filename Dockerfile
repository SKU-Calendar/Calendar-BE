# ---- build stage ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY . .

# gradle wrapper 실행 권한 부여 (리눅스에서 필요)
RUN chmod +x ./gradlew

# wrapper로 빌드 (프로젝트가 요구하는 Gradle 버전 사용)
RUN ./gradlew clean bootJar -x test

# ---- run stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

ENV PORT=8080
EXPOSE 8080
CMD ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
