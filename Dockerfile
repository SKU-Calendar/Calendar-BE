# ---- build stage ----
FROM gradle:8.10.2-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle clean bootJar -x test

# ---- run stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Render는 PORT 환경변수를 줌. 스프링이 그 포트를 쓰게 해야 함.
ENV PORT=8080
EXPOSE 8080
CMD ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
