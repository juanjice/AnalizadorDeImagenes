FROM gradle:8.10.2-jdk17-alpine AS build
WORKDIR /app

# Copy build files first for better caching
COPY build.gradle settings.gradle gradlew gradlew.bat ./
COPY gradle ./gradle

# Copy source
COPY src ./src

RUN ./gradlew --no-daemon clean bootJar -x test

FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=dev

EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]